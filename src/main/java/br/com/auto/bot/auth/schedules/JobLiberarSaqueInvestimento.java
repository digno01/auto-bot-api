package br.com.auto.bot.auth.schedules;

import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.repository.*;
import br.com.auto.bot.auth.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class JobLiberarSaqueInvestimento {


    @Autowired
    private InvestimentoRepository investimentoRepository;


    @Transactional
    //@Scheduled(cron = "0 */1 * * * *")
   @Scheduled(cron = "0 0 1 * * MON-FRI") // Segunda a Sexta às 3:00
    public void processarLiberacaoSaque() {
        log.info("Iniciando processamento de rendimentos diários: {}", LocalDateTime.now());


        try {
            LocalDateTime dataAtual = LocalDateTime.now();
            // Verifica se é dia útil
            if (!Util.isDiaUtil(dataAtual.toLocalDate())) {
                log.info("Processamento ignorado: data atual não é dia útil");
                return;
            }

            // Recupera os investimentos que atendem aos critérios
            List<Investimento> investimentos = investimentoRepository
                    .findByDataLiberacaoLessThanEqualAndIsLiberadoSaqueFalse(dataAtual);
            if (!investimentos.isEmpty()) {
                // Atualiza todos os investimentos recuperados
                investimentos.forEach(investimento -> {
                    investimento.setIsLiberadoSaque(true);
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
}
