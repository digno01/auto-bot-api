package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.InvestimentoRequestDTO;
import br.com.auto.bot.auth.dto.InvestimentoResponseDTO;
import br.com.auto.bot.auth.dto.InvestimentoResumoDTO;
import br.com.auto.bot.auth.enums.StatusInvestimento;
import br.com.auto.bot.auth.exceptions.BussinessException;
import br.com.auto.bot.auth.model.HistoricoTrocaRobo;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.RoboInvestidor;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.projection.InvestimentoResumoProjection;
import br.com.auto.bot.auth.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

            validarInvestimento(usuario, robo, request);

            if ("NOVO".equals(request.getTipoOperacao())) {
                return realizarNovoInvestimento(usuario, robo, request.getValorInvestimento());
            } else if ("TROCA".equals(request.getTipoOperacao())) {
                return realizarTrocaRobo(usuario, robo);
            } else {
                throw new BussinessException("Tipo de operação inválido");
            }

        } catch (Exception e) {
            log.error("Erro ao processar investimento: {}", e.getMessage());
            throw new BussinessException(e.getMessage());
        }
    }

    private void validarInvestimento(User usuario, RoboInvestidor robo, InvestimentoRequestDTO request) {
        // Validação de valor mínimo e máximo
        if (request.getValorInvestimento().compareTo(robo.getValorInvestimentoMin()) < 0) {
            throw new BussinessException("Valor abaixo do mínimo permitido para este robô");
        }
        if (request.getValorInvestimento().compareTo(robo.getValorInvestimentoMax()) > 0) {
            throw new BussinessException("Valor acima do máximo permitido para este robô");
        }

        // Validação de saldo disponível para novo investimento
        if ("NOVO".equals(request.getTipoOperacao())) {
            if (usuario.getSaldoDisponivel().compareTo(request.getValorInvestimento()) < 0) {
                throw new BussinessException("Saldo disponível insuficiente");
            }
            return;
        }

        // Validações apenas para troca de robô
        if ("TROCA".equals(request.getTipoOperacao())) {
            Optional<Investimento> investimentoAtivo = investimentoRepository
                    .findByUsuarioAndStatus(usuario, StatusInvestimento.A);

            if (investimentoAtivo.isEmpty()) {
                throw new BussinessException("Não há investimento ativo para realizar a troca");
            }

            if (LocalDateTime.now().isBefore(
                    investimentoAtivo.get().getDataInicio()
                            .plusDays(investimentoAtivo.get().getRoboInvestidor().getDiasPeriodo()))) {
                throw new BussinessException("Período mínimo não atingido para troca de robô");
            }
        }
    }

    private InvestimentoResponseDTO realizarNovoInvestimento(
            User usuario,
            RoboInvestidor robo,
            BigDecimal valor) {

        Optional<Investimento> investimentoExistente = investimentoRepository
                .findByUsuarioAndStatus(usuario, StatusInvestimento.A);

        if (investimentoExistente.isPresent()) {
            Investimento investimentoAtual = investimentoExistente.get();
            investimentoAtual.setValorInvestido(investimentoAtual.getValorInvestido().add(valor));
            investimentoRepository.save(investimentoAtual);

            usuario.setSaldoDisponivel(usuario.getSaldoDisponivel().subtract(valor));
            usuario.setSaldoInvestido(usuario.getSaldoInvestido().add(valor));
            userRepository.save(usuario);

            return InvestimentoResponseDTO.fromEntity(investimentoAtual);
        } else {
            Investimento novoInvestimento = new Investimento();
            novoInvestimento.setUsuario(usuario);
            novoInvestimento.setRoboInvestidor(robo);
            novoInvestimento.setValorInvestido(valor);
            novoInvestimento.setStatus(StatusInvestimento.A);
            novoInvestimento.setPercentualRendimentoDiario(calcularPercentualRendimentoDiario(robo));

            usuario.setSaldoDisponivel(usuario.getSaldoDisponivel().subtract(valor));
            usuario.setSaldoInvestido(usuario.getSaldoInvestido().add(valor));
            userRepository.save(usuario);

            return InvestimentoResponseDTO.fromEntity(investimentoRepository.save(novoInvestimento));
        }
    }

    private InvestimentoResponseDTO realizarTrocaRobo(User usuario, RoboInvestidor novoRobo) {
        Investimento investimentoAtual = investimentoRepository
                .findByUsuarioAndStatus(usuario, StatusInvestimento.A)
                .orElseThrow(() -> new BussinessException("Investimento ativo não encontrado"));

        BigDecimal saldoTotal = usuario.getSaldoInvestido().add(usuario.getSaldoRendimentos());

        // Registra histórico
        HistoricoTrocaRobo historico = new HistoricoTrocaRobo();
        historico.setUsuario(usuario);
        historico.setRoboOrigem(investimentoAtual.getRoboInvestidor());
        historico.setRoboDestino(novoRobo);
        historico.setSaldoTransferido(usuario.getSaldoInvestido());
        historico.setRendimentosIncorporados(usuario.getSaldoRendimentos());
        historicoRepository.save(historico);

        // Finaliza investimento atual
        investimentoAtual.setStatus(StatusInvestimento.F);
        investimentoAtual.setDataFim(LocalDateTime.now());
        investimentoRepository.save(investimentoAtual);

        // Cria novo investimento
        Investimento novoInvestimento = new Investimento();
        novoInvestimento.setUsuario(usuario);
        novoInvestimento.setRoboInvestidor(novoRobo);
        novoInvestimento.setValorInvestido(saldoTotal);
        novoInvestimento.setStatus(StatusInvestimento.A);
        novoInvestimento.setPercentualRendimentoDiario(calcularPercentualRendimentoDiario(novoRobo));

        // Atualiza saldos
        usuario.setSaldoInvestido(saldoTotal);
        usuario.setSaldoRendimentos(BigDecimal.ZERO);
        userRepository.save(usuario);

        return InvestimentoResponseDTO.fromEntity(investimentoRepository.save(novoInvestimento));
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
}
