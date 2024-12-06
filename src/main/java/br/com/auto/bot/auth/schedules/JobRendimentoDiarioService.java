package br.com.auto.bot.auth.schedules;

import br.com.auto.bot.auth.dto.CriptoData;
import br.com.auto.bot.auth.dto.QrCodeRequestDTO;
import br.com.auto.bot.auth.enums.StatusInvestimento;
import br.com.auto.bot.auth.enums.TipoRendimento;
import br.com.auto.bot.auth.enums.TipoResultado;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.*;
import br.com.auto.bot.auth.repository.*;
import br.com.auto.bot.auth.service.CriptoService;
import br.com.auto.bot.auth.service.PaymentGatewayService;
import br.com.auto.bot.auth.util.Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class JobRendimentoDiarioService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RendimentoRepository rendimentoRepository;

    @Autowired
    private IndicacaoRepository indicacaoRepository;

    @Autowired
    private InvestimentoRepository investimentoRepository;

    @Autowired
    private NivelIndicacaoRepository nivelIndicacaoRepository;

    @Autowired
    private CriptoService criptoService;
    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private OperacaoCriptoRepository operacaoCriptoRepository;
    @Autowired
    private RoboInvestidorRepository roboInvestidorRepository;


    @Value("${rendimento.probabilidade-lucro:0.70}")
    private double probabilidadeLucro;

    private List<CriptoData> criptosDiarios;

    @Transactional
//    @Scheduled(cron = "0 */30 * * * *")
//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 1 * * MON-FRI") // Segunda a Sexta às 1:00
    public void processarRendimentosDiarios() {
        log.info("Iniciando processamento de rendimentos diários: {}", LocalDateTime.now());

        LocalDateTime dataAtual = LocalDateTime.now();

        // Verifica se é dia útil
        if (!Util.isDiaUtil(dataAtual.toLocalDate())) {
            log.info("Processamento ignorado: data atual não é dia útil");
            return;
        }

        try {
            criptosDiarios = criptoService.buscarDadosCripto24h();
            if (criptosDiarios.isEmpty()) {
                log.warn("Nenhuma cripto com lucro nas últimas 24h");
                return;
            }

            List<User> usuarios = userRepository.findUsersWithActiveInvestmentsAndPositiveBalance(BigDecimal.ZERO);

            for (User usuario : usuarios) {
                processarRendimentoUsuario(usuario);
            }

            log.info("Processamento de rendimentos concluído com sucesso.");

        } catch (Exception e) {
            log.error("Erro ao processar rendimentos diários: " + e.getMessage(), e);
            throw new BusinessException("Erro no processamento de rendimentos", e);
        }
    }


    private BigDecimal ajustarPercentualRendimento(
            BigDecimal percentualBase,
            Boolean isUltimoLoss,
            BigDecimal valorUltimoRendimento) {

        if (isUltimoLoss != null && isUltimoLoss) {
            BigDecimal percentualLossAnterior = valorUltimoRendimento
                    .abs()
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            return percentualBase.add(percentualLossAnterior.divide(BigDecimal.valueOf(2)));
        }

        return percentualBase;
    }

    private BigDecimal limitarPercentualRobo(BigDecimal percentual, RoboInvestidor robo) {
        if (percentual.compareTo(robo.getPercentualRendimentoMin()) < 0) {
            return robo.getPercentualRendimentoMin();
        }
        if (percentual.compareTo(robo.getPercentualRendimentoMax()) > 0) {
            return robo.getPercentualRendimentoMax();
        }
        return percentual;
    }
    @Transactional
    public void processarRendimentoUsuario(User usuario) {
        try {

            LocalDateTime dataAtual = LocalDateTime.now();
            LocalDateTime dataLimite = dataAtual.minusDays(1);

            List<Investimento> listInvestimentos = investimentoRepository
                    .findAllInvestimentosAtivosComSaldoByUsuarioId(
                            usuario.getId(),
                            dataLimite,
                            dataAtual
                    );

            //List<Investimento> listInvestimentos = investimentoRepository.findAllInvestimentosAtivosComSaldoByUsuarioId(usuario.getId());

            listInvestimentos.forEach(investimento -> {
                RoboInvestidor robo = investimento.getRoboInvestidor();
                boolean isLucro = Math.random() < probabilidadeLucro;
                double percentualDoDia = isLucro ?
                        investimento.getRoboInvestidor().getPercentualRendimentoMin().doubleValue() + (Math.random() * (robo.getPercentualRendimentoMax().subtract(robo.getPercentualRendimentoMin())).doubleValue()) :
                        (Double.valueOf(-0.01));
                // se for juros sobre juros
               // BigDecimal saldoRendimentos = investimento.getSaldoAtual();
                //se for so em cima do capital
                BigDecimal saldoRendimentos = investimento.getValorInicial();
                BigDecimal rendimentoBruto = saldoRendimentos.multiply(BigDecimal.valueOf(percentualDoDia))
                        .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_DOWN);

                addGatwaySaldoRendimento(usuario, rendimentoBruto, investimento.getRoboInvestidor().getId());
                BigDecimal saldoAcumulado = investimento.getSaldoAtual().add(rendimentoBruto);
                if (saldoAcumulado.compareTo(BigDecimal.ZERO) < 0) {
                    BigDecimal saldoEfetivo = saldoAcumulado;

                    if (saldoEfetivo.compareTo(BigDecimal.ZERO) <= 0) {
                        liquidarInvestimento(usuario, investimento, saldoEfetivo);
                        return;
                    }
                }
                investimento.setSaldoAtual(saldoAcumulado);
                investimento.setIsUltimoRendimentoLoss(saldoAcumulado.compareTo(BigDecimal.ZERO) < 0);



                List<NivelIndicador> niveisIndicadores = buscarIndicadoresAcima(usuario);
                BigDecimal valorTotalIndicadores = calcularValorParaIndicadores(rendimentoBruto, niveisIndicadores);
                BigDecimal rendimentoLiquido = rendimentoBruto.subtract(valorTotalIndicadores);
                Rendimento rendimento = new Rendimento();
                rendimento.setUsuario(usuario);
                rendimento.setInvestimento(investimento);
                rendimento.setValorRendimento(rendimentoLiquido);
                rendimento.setTipoRendimento(TipoRendimento.I);
                rendimento.setPercentualRendimento(BigDecimal.valueOf(percentualDoDia));
                rendimento.setValorAcumulado(saldoAcumulado);
                rendimento.setTipoResultado(rendimentoLiquido.compareTo(BigDecimal.ZERO) >= 0 ?
                        TipoResultado.LUCRO : TipoResultado.PERDA);
                rendimentoRepository.save(rendimento);

                registrarOperacoesCripto(rendimento);

                robo.setValorGanho(robo.getValorGanho().add(saldoAcumulado));
                roboInvestidorRepository.save(robo);
                if (investimento.getSaldoAtual().compareTo(BigDecimal.ZERO) <= 0) {
                    cancelarInvestimento(usuario, investimento);
                    return;
                }
                //distribuirRendimentosIndicadores(niveisIndicadores, rendimentoBruto, investimento);

            });

        } catch (Exception e) {
            log.error("Erro ao processar rendimento para usuário {}: {}", usuario.getId(), e.getMessage());
            throw e;
        }
    }

    private void addGatwaySaldoRendimento(User usuario, BigDecimal rendimentoBruto, Long id) {
        QrCodeRequestDTO requestDTO = new QrCodeRequestDTO();
        requestDTO.setAmount(rendimentoBruto);
        requestDTO.setIdRobo(id);
        paymentGatewayService.addRendimentoInvestidorGatway(requestDTO, usuario);
    }

    private void cancelarInvestimento(User usuario, Investimento investimento) {
        // Registra o rendimento final
        Rendimento rendimentoCancelamento = new Rendimento();
        rendimentoCancelamento.setUsuario(usuario);
        rendimentoCancelamento.setInvestimento(investimento);
        rendimentoCancelamento.setValorRendimento(BigDecimal.ZERO);
        rendimentoCancelamento.setTipoRendimento(TipoRendimento.L);
        rendimentoCancelamento.setPercentualRendimento(BigDecimal.valueOf(100));
        rendimentoCancelamento.setTipoResultado(TipoResultado.PERDA);
        rendimentoRepository.save(rendimentoCancelamento);
        // Atualiza o status do investimento para CANCELADO
        investimento.setStatus(StatusInvestimento.C);
        investimentoRepository.save(investimento);
        userRepository.save(usuario);

        log.warn("Investimento cancelado por saldo insuficiente - Usuário: {}", usuario.getId());
    }


    private void registrarOperacoesCripto(Rendimento rendimento) {
        if (criptosDiarios == null || criptosDiarios.isEmpty()) {
            log.warn("Dados de criptomoedas não disponíveis");
            return;
        }

        BigDecimal valorRendimento = rendimento.getValorRendimento().abs();
        if (valorRendimento.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Valor do rendimento é zero, pulando registro de operações cripto");
            return;
        }

        int numeroOperacoes = new Random().nextInt(2) + 1; // 1 ou 2 operações
        List<BigDecimal> valoresBase = distribuirValorCompra(
                valorRendimento.multiply(BigDecimal.valueOf(3)), // Base maior para ter margem
                numeroOperacoes
        );

        boolean isLucro = rendimento.getTipoResultado().equals(TipoResultado.LUCRO);

        for (int i = 0; i < numeroOperacoes && i < criptosDiarios.size(); i++) {
            CriptoData cripto = criptosDiarios.get(i);
            BigDecimal valorBase = valoresBase.get(i);
            if (valorBase.compareTo(BigDecimal.ZERO) == 0) {
                continue; // Pula se o valor base for zero
            }

            BigDecimal valorVariacao = valorRendimento.multiply(
                    BigDecimal.valueOf(1.0 / numeroOperacoes)
            ).setScale(8, RoundingMode.HALF_DOWN);

            try {
                if (isLucro) {
                    BigDecimal valorCompra = valorBase;
                    BigDecimal valorVenda = valorCompra.add(valorVariacao);
                    registrarOperacaoAjustada(rendimento, cripto, valorCompra, valorVenda);
                } else {
                    // Para perda: valores positivos, mas lucro negativo
                    BigDecimal valorCompra = valorBase;
                    BigDecimal valorVenda = valorCompra.subtract(valorVariacao);
                    registrarOperacaoAjustada(rendimento, cripto, valorCompra, valorVenda);
                }
            } catch (Exception e) {
                log.error("Erro ao registrar operação cripto: " + e.getMessage());
            }
        }
    }

    private void registrarOperacaoAjustada(Rendimento rendimento, CriptoData cripto,
                                           BigDecimal valorCompra, BigDecimal valorVenda) {
        try {
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime horaCompra = agora.minusHours(new Random().nextInt(8) + 1);
            LocalDateTime horaVenda = horaCompra.plusHours(new Random().nextInt(6) + 1);

            // Garante que os valores não sejam zero
            if (valorCompra.compareTo(BigDecimal.ZERO) == 0) {
                valorCompra = BigDecimal.ONE;
            }
            if (valorVenda.compareTo(BigDecimal.ZERO) == 0) {
                valorVenda = BigDecimal.ONE;
            }

            BigDecimal lucro = valorVenda.subtract(valorCompra);

            // Calcula o percentual de variação com proteção contra divisão por zero
            BigDecimal percentualVariacao;
            try {
                percentualVariacao = lucro.multiply(BigDecimal.valueOf(100))
                        .divide(valorCompra.abs(), 2, RoundingMode.HALF_DOWN);
            } catch (ArithmeticException e) {
                percentualVariacao = BigDecimal.ZERO;
            }

            BigDecimal precoMedio = cripto.getCurrentPrice();
            if (precoMedio == null || precoMedio.compareTo(BigDecimal.ZERO) <= 0) {
                precoMedio = BigDecimal.ONE;
            }

            BigDecimal quantidadeMoeda;
            try {
                quantidadeMoeda = valorCompra.abs().divide(precoMedio, 8, RoundingMode.HALF_DOWN);
            } catch (ArithmeticException e) {
                quantidadeMoeda = BigDecimal.ONE;
            }

            OperacaoCripto operacao = new OperacaoCripto();
            operacao.setRendimento(rendimento);
            operacao.setMoeda(cripto.getSymbol());
            operacao.setValorCompra(valorCompra);
            operacao.setValorVenda(valorVenda);
            operacao.setQuantidadeMoeda(quantidadeMoeda);
            operacao.setDataCompra(horaCompra);
            operacao.setDataVenda(horaVenda);
            operacao.setPercentualVariacao(percentualVariacao);
            operacao.setValorLucro(lucro);
            operacao.setUrlImagem(cripto.getImage());

            operacaoCriptoRepository.save(operacao);
        } catch (Exception e) {
            log.error("Erro ao registrar operação ajustada: " + e.getMessage());
            throw e;
        }
    }

    private List<BigDecimal> distribuirValorCompra(BigDecimal total, int partes) {
        List<BigDecimal> distribuicao = new ArrayList<>();
        BigDecimal restante = total;

        for (int i = 0; i < partes - 1; i++) {
            BigDecimal parte = restante.multiply(
                    BigDecimal.valueOf(0.4 + Math.random() * 0.2)
            ).setScale(8, RoundingMode.HALF_DOWN);
            distribuicao.add(parte);
            restante = restante.subtract(parte);
        }

        distribuicao.add(restante);
        return distribuicao;
    }

    // ... (métodos auxiliares existentes permanecem iguais)

    @Data
    @AllArgsConstructor
    private static class NivelIndicador {
        private User indicador;
        private NivelIndicacao nivelIndicacao;
    }

    private void distribuirRendimentosIndicadores(
            List<NivelIndicador> indicadores,
            BigDecimal rendimentoBruto,
            Investimento investimento) {
        for (NivelIndicador ni : indicadores) {
            BigDecimal percentualNivel = ni.getNivelIndicacao().getPercentualRendimento();
            BigDecimal valorRendimentoIndicacao = rendimentoBruto
                    .multiply(percentualNivel)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Registra o rendimento do indicador
            Rendimento rendimentoIndicacao = new Rendimento();
            rendimentoIndicacao.setUsuario(ni.getIndicador());
            rendimentoIndicacao.setInvestimento(investimento);
            rendimentoIndicacao.setValorRendimento(valorRendimentoIndicacao);
            rendimentoIndicacao.setTipoRendimento(getTipoRendimentoPorNivel(ni.getNivelIndicacao().getNivel()));
            rendimentoIndicacao.setPercentualRendimento(percentualNivel);
            rendimentoIndicacao.setTipoResultado(valorRendimentoIndicacao.compareTo(BigDecimal.ZERO) >= 0 ?
                    TipoResultado.LUCRO : TipoResultado.PERDA);
            rendimentoRepository.save(rendimentoIndicacao);
//
//            // Atualiza saldo do indicador
//            ni.getIndicador().setSaldoRendimentos(
//                    ni.getIndicador().getSaldoRendimentos().add(valorRendimentoIndicacao)
//            );
            userRepository.save(ni.getIndicador());
        }
    }

    private TipoRendimento getTipoRendimentoPorNivel(Integer nivel) {
        switch (nivel) {
            case 1: return TipoRendimento.N1;
            case 2: return TipoRendimento.N2;
            case 3: return TipoRendimento.N3;
            default: throw new IllegalArgumentException("Nível de indicação inválido: " + nivel);
        }
    }

    private void liquidarInvestimento(User usuario, Investimento investimento, BigDecimal saldoEfetivo) {
        // Registra o rendimento de loss total
        Rendimento rendimentoLoss = new Rendimento();
        rendimentoLoss.setUsuario(usuario);
        rendimentoLoss.setInvestimento(investimento);
        rendimentoLoss.setValorRendimento(saldoEfetivo.negate()); // Valor negativo do saldo efetivo
        rendimentoLoss.setTipoRendimento(TipoRendimento.L);
        rendimentoLoss.setPercentualRendimento(BigDecimal.valueOf(100)); // Loss total = 100%
        rendimentoLoss.setTipoResultado(TipoResultado.PERDA);
        rendimentoRepository.save(rendimentoLoss);

        // Finaliza o investimento
        investimento.setStatus(StatusInvestimento.F);
        investimentoRepository.save(investimento);
        userRepository.save(usuario);

        log.warn("Investimento liquidado por loss total - Usuário: {}, Valor: {}",
                usuario.getId(), saldoEfetivo);
    }

    private List<NivelIndicador> buscarIndicadoresAcima(User usuario) {
        List<NivelIndicador> indicadores = new ArrayList<>();
        User usuarioAtual = usuario;
        int nivelAtual = 1;

        while (nivelAtual <= 3) {
            Optional<Indicacao> indicacao = indicacaoRepository.findByUsuarioAndIsActiveTrue(usuarioAtual);
            if (indicacao.isPresent()) {
                User indicador = indicacao.get().getUsuarioIndicador();
                NivelIndicacao nivelIndicacao = nivelIndicacaoRepository
                        .findByNivelAndIsActiveTrue(nivelAtual)
                        .orElseThrow(() -> new RuntimeException("Nível de indicação não encontrado"));

                indicadores.add(new NivelIndicador(indicador, nivelIndicacao));
                usuarioAtual = indicador;
                nivelAtual++;
            } else {
                break;
            }
        }

        return indicadores;
    }


    private BigDecimal calcularValorParaIndicadores(BigDecimal rendimentoBruto, List<NivelIndicador> indicadores) {
        return indicadores.stream()
                .map(ni -> rendimentoBruto.multiply(ni.getNivelIndicacao().getPercentualRendimento())
                        .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_DOWN))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
