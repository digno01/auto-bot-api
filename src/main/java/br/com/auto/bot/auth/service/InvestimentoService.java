package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.*;
import br.com.auto.bot.auth.enums.StatusInvestimento;
import br.com.auto.bot.auth.exceptions.BussinessException;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.RoboInvestidor;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.projection.InvestimentoResumoProjection;
import br.com.auto.bot.auth.repository.*;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class InvestimentoService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoboInvestidorRepository roboRepository;

    @Autowired
    private InvestimentoRepository investimentoRepository;

    @Autowired
    private HistoricoTrocaRoboRepository historicoRepository;

    @Autowired
    private RendimentoRepository rendimentoRepository;


    public InvestimentoResponseDTO processarInvestimento(InvestimentoRequestDTO request) {
        try {
            User usuario = userRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new BussinessException("Usuário não encontrado"));

            RoboInvestidor robo = roboRepository.findById(request.getRoboId())
                    .orElseThrow(() -> new BussinessException("Robô não encontrado"));

            validarValorMaximoMinimoInvestimento(robo, request.getValorInvestimento());
            return realizarNovoInvestimento(usuario, robo, request);
            //if ("NOVO".equals(request.getTipoOperacao())) {
            /*} else if ("TROCA".equals(request.getTipoOperacao())) {
                return realizarTrocaRobo(usuario, robo);*/
            /*} else {
                throw new BussinessException("Tipo de operação inválido");
            }*/

        } catch (Exception e) {
            log.error("Erro ao processar investimento: {}", e.getMessage());
            throw new BussinessException(e.getMessage());
        }
    }

    private void validarValorMaximoMinimoInvestimento(RoboInvestidor robo, BigDecimal valorInvestimento) {
        // Validação de valor mínimo e máximo
        if (valorInvestimento.compareTo(robo.getValorInvestimentoMin()) < 0) {
            throw new BussinessException("Valor abaixo do mínimo permitido para este robô");
        }
        if (valorInvestimento.compareTo(robo.getValorInvestimentoMax()) > 0) {
            throw new BussinessException("Valor acima do máximo permitido para este robô");
        }
    }

    private InvestimentoResponseDTO realizarNovoInvestimento(
            User usuario,
            RoboInvestidor robo,
            InvestimentoRequestDTO request) {

        Optional<Investimento> investimentoExistente = investimentoRepository
                .findByUsuarioAndStatus(usuario, StatusInvestimento.A);
        if (investimentoExistente.isPresent()) {
            validarValorUtrapassaComInvestimentoAtivo(investimentoExistente.get(), request.getValorInvestimento(), robo);
            Investimento investimentoAtual = investimentoExistente.get();
            Investimento novoInvestimento = new Investimento();

            BigDecimal valorAcumulado = investimentoAtual.getSaldoAtual().add(request.getValorInvestimento());
            novoInvestimento.setValorInicial(valorAcumulado);
            novoInvestimento.setSaldoAtual(valorAcumulado);
            novoInvestimento.setDataLiberacao(LocalDateTime.now().plusDays(robo.getDiasPeriodo()));
            novoInvestimento.setStatus(StatusInvestimento.P);
            novoInvestimento.setIdTransacaoPagamentoGateway(request.getIdTransacaoPagamentoGateway());
            novoInvestimento.setUrlQrcode(request.getUrlQrcode());
            investimentoRepository.save(novoInvestimento);

            investimentoAtual.setStatus(StatusInvestimento.R);
            investimentoRepository.save(investimentoAtual);
            return InvestimentoResponseDTO.fromEntity(investimentoAtual);
        } else {
            Investimento novoInvestimento = new Investimento();
            novoInvestimento.setUsuario(usuario);
            novoInvestimento.setRoboInvestidor(robo);
            novoInvestimento.setValorInicial(request.getValorInvestimento());
            novoInvestimento.setStatus(StatusInvestimento.P);
            novoInvestimento.setDataLiberacao(LocalDateTime.now().plusDays(robo.getDiasPeriodo()));
            novoInvestimento.setIdTransacaoPagamentoGateway(request.getIdTransacaoPagamentoGateway());
            novoInvestimento.setUrlQrcode(request.getUrlQrcode());
            userRepository.save(usuario);

            return InvestimentoResponseDTO.fromEntity(investimentoRepository.save(novoInvestimento));
        }
    }

    private void validarValorUtrapassaComInvestimentoAtivo(Investimento investimento, BigDecimal valor, RoboInvestidor robo) {
        BigDecimal valorAcumulado = investimento.getSaldoAtual().add(valor);
        validarValorMaximoMinimoInvestimento(robo, valorAcumulado);
    }

    private BigDecimal calcularPercentualRendimentoDiario(RoboInvestidor robo) {
        BigDecimal range = robo.getPercentualRendimentoMax()
                .subtract(robo.getPercentualRendimentoMin());
        BigDecimal randomFactor = BigDecimal.valueOf(Math.random());
        return robo.getPercentualRendimentoMin().add(range.multiply(randomFactor));
    }

    @Transactional(readOnly = true)
    public List<InvestimentoResponseDTO> buscarInvestimentosAtivos(Long usuarioId) {
        return investimentoRepository.findByUsuarioIdAndStatus(usuarioId, StatusInvestimento.A)
                .stream()
                .map(InvestimentoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InvestimentoResumoDTO> gerarResumoInvestimentos(
            String status,
            LocalDateTime inicio,
            LocalDateTime fim) {
        try {
            List<InvestimentoResumoProjection> projections =
                    investimentoRepository.gerarResumoInvestimentos(status, inicio, fim);

            return projections.stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Erro ao gerar resumo de investimentos: {}", e.getMessage());
            throw new RuntimeException("Erro ao gerar resumo de investimentos", e);
        }
    }

    private InvestimentoResumoDTO converterParaDTO(InvestimentoResumoProjection projection) {
        return new InvestimentoResumoDTO(
                projection.getNomeRobo(),
                projection.getQuantidadeInvestimentos(),
                projection.getValorTotalInvestido(),
                projection.getMediaPercentualRendimento()
        );
    }

    public BigDecimal calcularSaldoParaSaque(Long usuarioId) {
        List<Investimento> investimentosAtivos = investimentoRepository.findByUsuarioIdAndStatusAndDataLiberacaoLessThanEqualAndSaldoAtualGreaterThan(
                usuarioId,
                StatusInvestimento.A,
                LocalDateTime.now(),
                BigDecimal.ZERO
        );

        return investimentosAtivos.stream()
                .map(Investimento::getSaldoAtual)
                .filter(saldo -> saldo.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public List<InvestimentoSaqueDTO> listarInvestimentosParaSaque(Long usuarioId) {
        List<Investimento> investimentos = investimentoRepository.findByUsuarioIdAndSaldoAtualGreaterThanAndDataLiberacaoLessThanEqualAndStatus(
                usuarioId,
                BigDecimal.ZERO,
                LocalDateTime.now(),
                StatusInvestimento.A
        );

        return investimentos.stream()
                .map(investimento -> new InvestimentoSaqueDTO(
                        investimento.getId(),
                        investimento.getRoboInvestidor().getNome(),
                        investimento.getDataInvestimento(),
                        investimento.getDataLiberacao(),
                        investimento.getValorInicial(),
                        investimento.getSaldoAtual()
                ))
                .collect(Collectors.toList());
    }

    public void permiteUsuarioInvestir(QrCodeRequestDTO qrCodeRequestDTO) {
        User usuario = userRepository.findById(ObterDadosUsuarioLogado.getUsuarioLogadoId())
                .orElseThrow(() -> new BussinessException("Usuário não encontrado"));

        RoboInvestidor robo = roboRepository.findById(qrCodeRequestDTO.getIdRobo())
                .orElseThrow(() -> new BussinessException("Robô não encontrado"));
        Optional<Investimento> optInvestimento =  investimentoRepository.findByUsuarioAndRoboAndStatusNotFinalizedOrCanceled(usuario, robo);
        if(optInvestimento.isEmpty()){
            return;
        }
        Investimento investimento = optInvestimento.get();
        if(investimento.getStatus().equals(StatusInvestimento.A)){
            BigDecimal valorAcumulado = investimento.getSaldoAtual().add(qrCodeRequestDTO.getAmount());
            validarValorMaximoMinimoInvestimento(robo, valorAcumulado);
        }else if(investimento.getStatus().equals(StatusInvestimento.P) || investimento.getStatus().equals(StatusInvestimento.R)){
            throw new BussinessException(ObterDadosUsuarioLogado.obterDadosUsuarioLogado().getNome() + " você já tem um investimento para o robô aguardando pagamento.");
        }


    }
}
