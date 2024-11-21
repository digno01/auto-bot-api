package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.enums.StatusInvestimento;
import br.com.auto.bot.auth.enums.TipoRendimento;
import br.com.auto.bot.auth.enums.TipoResultado;
import br.com.auto.bot.auth.model.*;
import br.com.auto.bot.auth.repository.*;
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

@Service
@Slf4j
public class RendimentoDiarioService {

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

    @Value("${rendimento.percentual-min-lucro:5.0}")
    private double percentualMinLucro;

    @Value("${rendimento.percentual-max-lucro:13.0}")
    private double percentualMaxLucro;

    @Value("${rendimento.percentual-prejuizo:-3.0}")
    private double percentualPrejuizo;

    @Value("${rendimento.probabilidade-lucro:0.70}")
    private double probabilidadeLucro;

    @Transactional
    //@Scheduled(cron = "0 06 3 * * *") // Executa todos os dias às 02:30
    @Scheduled(cron = "0 */1 * * * *")
    public void processarRendimentosDiarios() {
        log.info("Iniciando processamento de rendimentos diários: {}", LocalDateTime.now());

        try {
            // Determina se hoje será lucro ou prejuízo para todos
            boolean isLucro = Math.random() < probabilidadeLucro;


//            log.info("Resultado do dia: {} - Percentual: {:.2f}%",
//                    isLucro ? "LUCRO" : "PREJUÍZO",
//                    percentualDoDia);

            // Busca todos os usuários ativos com saldo investido
            List<User> usuarios = userRepository.findByIsActiveTrueAndSaldoInvestidoGreaterThan(BigDecimal.ZERO);

            for (User usuario : usuarios) {

                // Calcula o percentual do dia
                double percentualDoDia = isLucro ?
                        percentualMinLucro + (Math.random() * (percentualMaxLucro - percentualMinLucro)) :
                        percentualPrejuizo;
                processarRendimentoUsuario(usuario, BigDecimal.valueOf(percentualDoDia));
            }

            log.info("Processamento de rendimentos concluído com sucesso.");

        } catch (Exception e) {
            log.error("Erro ao processar rendimentos diários: " + e.getMessage(), e);
            throw new RuntimeException("Erro no processamento de rendimentos", e);
        }
    }

    private Investimento buscarOuCriarInvestimentoAtivo(User usuario) {
        return investimentoRepository.findFirstByUsuarioAndStatusOrderByDataInicioDesc(usuario, StatusInvestimento.A)
                .orElseGet(() -> {
                    Investimento novoInvestimento = new Investimento();
                    novoInvestimento.setUsuario(usuario);
                    novoInvestimento.setValorInvestido(usuario.getSaldoInvestido());
                    novoInvestimento.setStatus(StatusInvestimento.A);
                    novoInvestimento.setDataInicio(LocalDateTime.now());
                    return investimentoRepository.save(novoInvestimento);
                });
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
    public void processarRendimentoUsuario(User usuario, BigDecimal percentualDoDia) {
        try {
            Investimento investimento = buscarOuCriarInvestimentoAtivo(usuario);
            RoboInvestidor robo = investimento.getRoboInvestidor();

            BigDecimal percentualAjustado = ajustarPercentualRendimento(
                    percentualDoDia,
                    investimento.getIsUltimoRendimentoLoss(),
                    investimento.getValorUltimoRendimento()
            );

            percentualAjustado = limitarPercentualRobo(percentualAjustado, robo);

            BigDecimal saldoInvestido = usuario.getSaldoInvestido();
            BigDecimal saldoRendimentos = usuario.getSaldoRendimentos();

            // Calcula o rendimento bruto
            BigDecimal rendimentoBruto = saldoInvestido.multiply(percentualDoDia)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Verifica se o rendimento é negativo
            if (rendimentoBruto.compareTo(BigDecimal.ZERO) < 0) {
                // Calcula o saldo efetivo (investido - rendimentos negativos)
                BigDecimal saldoEfetivo = saldoInvestido.add(saldoRendimentos);

                // Se o saldo efetivo for zero ou negativo, liquida o investimento
                if (saldoEfetivo.compareTo(BigDecimal.ZERO) <= 0) {
                    liquidarInvestimento(usuario, investimento, saldoEfetivo);
                    return;
                }
            }

            // Continua com o processamento normal se não houve liquidação
            investimento.setValorUltimoRendimento(rendimentoBruto);
            investimento.setIsUltimoRendimentoLoss(rendimentoBruto.compareTo(BigDecimal.ZERO) < 0);

            List<NivelIndicador> niveisIndicadores = buscarIndicadoresAcima(usuario);
            BigDecimal valorTotalIndicadores = calcularValorParaIndicadores(rendimentoBruto, niveisIndicadores);
            BigDecimal rendimentoLiquido = rendimentoBruto.subtract(valorTotalIndicadores);

            // Registra o rendimento
            Rendimento rendimento = new Rendimento();
            rendimento.setUsuario(usuario);
            rendimento.setInvestimento(investimento);
            rendimento.setValorRendimento(rendimentoLiquido);
            rendimento.setTipoRendimento(TipoRendimento.I);
            rendimento.setPercentualRendimento(percentualDoDia);
            rendimento.setTipoResultado(rendimentoLiquido.compareTo(BigDecimal.ZERO) >= 0 ?
                    TipoResultado.LUCRO : TipoResultado.PERDA);
            rendimentoRepository.save(rendimento);
            rendimentoRepository.save(rendimento);

            // Atualiza saldo do usuário
            usuario.setSaldoRendimentos(usuario.getSaldoRendimentos().add(rendimentoLiquido));
            userRepository.save(usuario);

            // Distribui rendimentos para indicadores
            distribuirRendimentosIndicadores(niveisIndicadores, rendimentoBruto, investimento);

        } catch (Exception e) {
            log.error("Erro ao processar rendimento para usuário {}: {}", usuario.getId(), e.getMessage());
            throw e;
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
        investimento.setDataFim(LocalDateTime.now());
        investimentoRepository.save(investimento);

        // Zera os saldos do usuário
        usuario.setSaldoInvestido(BigDecimal.ZERO);
        usuario.setSaldoRendimentos(BigDecimal.ZERO);
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
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

            // Atualiza saldo do indicador
            ni.getIndicador().setSaldoRendimentos(
                    ni.getIndicador().getSaldoRendimentos().add(valorRendimentoIndicacao)
            );
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
    // Classe auxiliar para manter o indicador e seu nível juntos
    @Data
    @AllArgsConstructor
    private static class NivelIndicador {
        private User indicador;
        private NivelIndicacao nivelIndicacao;
    }
}

