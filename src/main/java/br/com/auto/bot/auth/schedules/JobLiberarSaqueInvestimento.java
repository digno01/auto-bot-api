package br.com.auto.bot.auth.schedules;

import br.com.auto.bot.auth.model.Indicacao;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.*;
import br.com.auto.bot.auth.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class JobLiberarSaqueInvestimento {


    @Autowired
    private InvestimentoRepository investimentoRepository;
    @Autowired
    private IndicacaoRepository indicacaoRepository;
    @Autowired
    private UserRepository userRepository;


    private static final BigDecimal NIVEL1_COMISSAO_1 = new BigDecimal("0.08");
    private static final BigDecimal NIVEL1_COMISSAO_2 = new BigDecimal("0.03");
    private static final BigDecimal NIVEL1_COMISSAO_3 = new BigDecimal("0.01");

    private static final BigDecimal NIVEL2_COMISSAO_1 = new BigDecimal("0.09");
    private static final BigDecimal NIVEL2_COMISSAO_2 = new BigDecimal("0.03");
    private static final BigDecimal NIVEL2_COMISSAO_3 = new BigDecimal("0.01");

    private static final BigDecimal NIVEL3_COMISSAO_1 = new BigDecimal("0.10");
    private static final BigDecimal NIVEL3_COMISSAO_2 = new BigDecimal("0.03");
    private static final BigDecimal NIVEL3_COMISSAO_3 = new BigDecimal("0.01");

    private static final BigDecimal NIVEL4_COMISSAO_1 = new BigDecimal("0.11");
    private static final BigDecimal NIVEL4_COMISSAO_2 = new BigDecimal("0.04");
    private static final BigDecimal NIVEL4_COMISSAO_3 = new BigDecimal("0.01");

    private static final BigDecimal NIVEL5_COMISSAO_1 = new BigDecimal("0.14");
    private static final BigDecimal NIVEL5_COMISSAO_2 = new BigDecimal("0.04");
    private static final BigDecimal NIVEL5_COMISSAO_3 = new BigDecimal("0.01");


    @Transactional
//    @Scheduled(cron = "0 * * * * *")
//    @Scheduled(cron = "0 0 1 * * MON-FRI") // Segunda a Sexta às 3:00
    @Scheduled(cron = "0 0 3 * * *") // às 3:00
    public void processarLiberacaoSaque() {
        log.info("Iniciando processamento de rendimentos diários: {}", LocalDateTime.now());


        try {
            LocalDateTime dataAtual = LocalDateTime.now();
            // Verifica se é dia útil
            /*if (!Util.isDiaUtil(dataAtual.toLocalDate())) {
                log.info("Processamento ignorado: data atual não é dia útil");
                return;
            }*/

            // Recupera os investimentos que atendem aos critérios
            List<Investimento> investimentos = investimentoRepository
                    .findByDataLiberacaoLessThanEqualAndIsLiberadoSaqueFalse(dataAtual);
            if (!investimentos.isEmpty()) {
                // Atualiza todos os investimentos recuperados
                investimentos.forEach(investimento -> {
                    investimento.setIsLiberadoSaque(true);
                    if(investimento.getValorDepositoComissao() != null){
                        pagaComissaoUsuario(investimento.getUsuario(), investimento.getValorDepositoComissao());
                    }
                });

                // Salva todas as alterações de uma vez
                investimentoRepository.saveAll(investimentos);

                log.info("Processamento de liberação de saque concluído. {} investimentos atualizados.",
                        investimentos.size());
            }

        } catch (Exception e) {
            log.error("Erro ao processar liberação de saque: {}", e.getMessage(), e);
            throw e;
        }
    }

    /*

    private void pagaComissaoUsuario(User usuario, BigDecimal valorComissao) {
        List<Indicacao> list = indicacaoRepository.findByUsuarioIndicadorAndIsActiveTrue(usuario);
        for (Indicacao indicacao : list) {
            User indicador = indicacao.getUsuarioIndicador();
            Integer nivel = indicador.getNivelConta();

        }

    }*/


    private void pagaComissaoUsuario(User usuario, BigDecimal valorComissao) {
        List<Indicacao> list = indicacaoRepository.findByUsuarioIndicadorAndIsActiveTrue(usuario);

        for (Indicacao indicacao : list) {
            User indicador = indicacao.getUsuarioIndicador();
            Integer nivelIndicador = indicador.getNivelConta();
            Integer nivelIndicacao = indicacao.getNivel();
            if(nivelIndicacao.equals(1)){
                BigDecimal valorComissaoCalculada = calcularComissao(valorComissao, nivelIndicador, nivelIndicacao);
                if (valorComissaoCalculada.compareTo(BigDecimal.ZERO) > 0) {
                    indicador.setSaldoComissao(indicador.getSaldoComissao().add(valorComissaoCalculada));
                    userRepository.save(indicador);
                }
            }
        }
    }

    private BigDecimal calcularComissao(BigDecimal valorBase, Integer nivelIndicador, Integer nivelIndicacao) {
        BigDecimal percentualComissao = getPercentualComissao(nivelIndicador, nivelIndicacao);
        return valorBase.multiply(percentualComissao).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getPercentualComissao(Integer nivelIndicador, Integer nivelIndicacao) {
        switch (nivelIndicador) {
            case 1:
                return getPercentualNivel1(nivelIndicacao);
            case 2:
                return getPercentualNivel2(nivelIndicacao);
            case 3:
                return getPercentualNivel3(nivelIndicacao);
            case 4:
                return getPercentualNivel4(nivelIndicacao);
            case 5:
                return getPercentualNivel5(nivelIndicacao);
            default:
                return BigDecimal.ZERO;
        }
    }

    private BigDecimal getPercentualNivel1(Integer nivelIndicacao) {
        switch (nivelIndicacao) {
            case 1:
                return NIVEL1_COMISSAO_1;
            case 2:
                return NIVEL1_COMISSAO_2;
            case 3:
                return NIVEL1_COMISSAO_3;
            default:
                return BigDecimal.ZERO;
        }
    }

    private BigDecimal getPercentualNivel2(Integer nivelIndicacao) {
        switch (nivelIndicacao) {
            case 1:
                return NIVEL2_COMISSAO_1;
            case 2:
                return NIVEL2_COMISSAO_2;
            case 3:
                return NIVEL2_COMISSAO_3;
            default:
                return BigDecimal.ZERO;
        }
    }

    private BigDecimal getPercentualNivel3(Integer nivelIndicacao) {
        switch (nivelIndicacao) {
            case 1:
                return NIVEL3_COMISSAO_1;
            case 2:
                return NIVEL3_COMISSAO_2;
            case 3:
                return NIVEL3_COMISSAO_3;
            default:
                return BigDecimal.ZERO;
        }
    }

    private BigDecimal getPercentualNivel4(Integer nivelIndicacao) {
        switch (nivelIndicacao) {
            case 1:
                return NIVEL4_COMISSAO_1;
            case 2:
                return NIVEL4_COMISSAO_2;
            case 3:
                return NIVEL4_COMISSAO_3;
            default:
                return BigDecimal.ZERO;
        }
    }

    private BigDecimal getPercentualNivel5(Integer nivelIndicacao) {
        switch (nivelIndicacao) {
            case 1:
                return NIVEL5_COMISSAO_1;
            case 2:
                return NIVEL5_COMISSAO_2;
            case 3:
                return NIVEL5_COMISSAO_3;
            default:
                return BigDecimal.ZERO;
        }
    }
}
