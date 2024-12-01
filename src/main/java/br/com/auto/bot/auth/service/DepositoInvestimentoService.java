package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.DepositoRequestDTO;
import br.com.auto.bot.auth.dto.DepositoResponseDTO;
import br.com.auto.bot.auth.enums.StatusDeposito;
import br.com.auto.bot.auth.enums.StatusInvestimento;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.Deposito;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.RoboInvestidor;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class DepositoInvestimentoService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepositoRepository depositoRepository;

    @Autowired
    private RoboInvestidorRepository roboRepository;

    @Autowired
    private InvestimentoRepository investimentoRepository;

    @Autowired
    private SaqueRepository saqueRepository;

    public DepositoResponseDTO processarDepositoEInvestimento(DepositoRequestDTO request, Long usuarioId) {
        try {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

            RoboInvestidor novoRobo = roboRepository.findById(request.getRoboId())
                    .orElseThrow(() -> new BusinessException("Robô não encontrado"));

            Optional<Investimento> investimentoAtivoOpt = investimentoRepository
                    .findInvestimentoAtivoComSaldoByUsuarioAndRobo(usuario, novoRobo, StatusInvestimento.A);

            // Validar limites considerando investimento existente
            validarLimitesInvestimento(investimentoAtivoOpt, novoRobo, request.getValorDeposito(), usuario);

            // Registrar depósito
            Deposito deposito = new Deposito();
            deposito.setUsuario(usuario);
            deposito.setValorDeposito(request.getValorDeposito());
            deposito.setStatus(StatusDeposito.P);
            deposito = depositoRepository.save(deposito);

            // Processar investimento
            Investimento novoInvestimento = processarInvestimento(usuario, novoRobo, request.getValorDeposito());

            return criarResponseDTO(deposito, novoInvestimento);

        } catch (Exception e) {
            log.error("Erro ao processar depósito e investimento: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    private void validarLimitesInvestimento(
            Optional<Investimento> investimentoAtivoOpt,
            RoboInvestidor novoRobo,
            BigDecimal valorDeposito,
            User usuario) {

        // Validação básica de valor mínimo
        if (valorDeposito.compareTo(novoRobo.getValorInvestimentoMin()) < 0) {
            throw new BusinessException(String.format(
                    "Valor do depósito (R$ %s) é inferior ao mínimo permitido para este robô (R$ %s)",
                    valorDeposito,
                    novoRobo.getValorInvestimentoMin()
            ));
        }

        if (investimentoAtivoOpt.isPresent()) {
            Investimento investimentoAtivo = investimentoAtivoOpt.get();
            RoboInvestidor roboAtual = investimentoAtivo.getRoboInvestidor();

            // Calcular total de saques do investimento atual
            BigDecimal totalSaques = calcularTotalSaquesInvestimento(investimentoAtivo);

            if (roboAtual.getId().equals(novoRobo.getId())) {
                // Mesmo robô - validar limite máximo considerando valor já investido
                BigDecimal totalAposDeposito = investimentoAtivo.getValorInicial()
                        .subtract(totalSaques)
                        .add(valorDeposito);

                if (totalAposDeposito.compareTo(novoRobo.getValorInvestimentoMax()) > 0) {
                    throw new BusinessException(String.format(
                            "Valor total após depósito (R$ %s) ultrapassaria o limite máximo do robô (R$ %s)",
                            totalAposDeposito,
                            novoRobo.getValorInvestimentoMax()
                    ));
                }
            } else {
                //TODO ajustar para o novo modelo de rendimentos
                // Robô diferente - validar limite considerando saldo total
                /*BigDecimal saldoTotalAposDeposito = usuario.getValorInicial()
                        .add(usuario.getSaldoRendimentos())
                        .subtract(totalSaques)
                        .add(valorDeposito);

                if (saldoTotalAposDeposito.compareTo(novoRobo.getValorInvestimentoMax()) > 0) {
                    throw new BussinessException(String.format(
                            "Valor total após troca de robô (R$ %s) ultrapassaria o limite máximo do novo robô (R$ %s)",
                            saldoTotalAposDeposito,
                            novoRobo.getValorInvestimentoMax()
                    ));
                }*/
            }
        } else {
            // Primeiro investimento - validar apenas limite máximo do robô
            if (valorDeposito.compareTo(novoRobo.getValorInvestimentoMax()) > 0) {
                throw new BusinessException(String.format(
                        "Valor do depósito (R$ %s) é superior ao máximo permitido para este robô (R$ %s)",
                        valorDeposito,
                        novoRobo.getValorInvestimentoMax()
                ));
            }
        }
    }

    private BigDecimal calcularTotalSaquesInvestimento(Investimento investimento) {
        return saqueRepository.findTotalSaquesByInvestimento(investimento.getId())
                .orElse(BigDecimal.ZERO);
    }


    private Investimento processarInvestimento(User usuario, RoboInvestidor novoRobo, BigDecimal valorDeposito) {
//        Optional<Investimento> investimentoAtivoOpt = investimentoRepository
//                .findInvestimentoAtivoComSaldoByUsuarioAndRobo(usuario, novoRobo, StatusInvestimento.A);
//
//        BigDecimal novoSaldoInvestido;
//
//        if (investimentoAtivoOpt.isPresent()) {
//            Investimento investimentoAtivo = investimentoAtivoOpt.get();
//            RoboInvestidor roboAtual = investimentoAtivo.getRoboInvestidor();
//
//            if (novoRobo.getDiasPeriodo() < roboAtual.getDiasPeriodo()) {
//                novoSaldoInvestido = investimentoAtivo.getSaldoAtual()
//                        .add(valorDeposito);
//            } else {
//                novoSaldoInvestido = investimentoAtivoOpt.get().getSaldoAtual().add(valorDeposito);
//            }
//
//            // Finalizar investimento atual
//            investimentoAtivo.setStatus(StatusInvestimento.F);
//            investimentoRepository.save(investimentoAtivo);
//        } else {
//            novoSaldoInvestido = valorDeposito;
//        }

        // Criar novo investimento

        Investimento novoInvestimento = new Investimento();
        novoInvestimento.setUsuario(usuario);
        novoInvestimento.setRoboInvestidor(novoRobo);
        novoInvestimento.setValorInicial(valorDeposito);
        novoInvestimento.setSaldoAtual(valorDeposito);
        novoInvestimento.setStatus(StatusInvestimento.A);
        novoInvestimento.setDataLiberacao(LocalDateTime.now().plusDays(novoRobo.getDiasPeriodo()));
        return investimentoRepository.save(novoInvestimento);
    }

    private BigDecimal calcularPercentualRendimentoDiario(RoboInvestidor robo) {
        BigDecimal range = robo.getPercentualRendimentoMax()
                .subtract(robo.getPercentualRendimentoMin());
        BigDecimal randomFactor = BigDecimal.valueOf(Math.random());
        return robo.getPercentualRendimentoMin().add(range.multiply(randomFactor));
    }

    private DepositoResponseDTO criarResponseDTO(Deposito deposito, Investimento investimento) {
        return new DepositoResponseDTO(
                deposito.getId(),
                investimento.getId(),
                deposito.getValorDeposito(),
                deposito.getStatus().getDescricao(),
                investimento.getStatus().getDescricao(),
                deposito.getDataDeposito(),
                investimento.getDataInvestimento().plusDays(
                        investimento.getRoboInvestidor().getDiasPeriodo()
                ),
                investimento.getRoboInvestidor().getNome()
        );
    }
}
