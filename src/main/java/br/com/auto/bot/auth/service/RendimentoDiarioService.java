package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.config.RendimentoConfig;
import br.com.auto.bot.auth.model.*;
import br.com.auto.bot.auth.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

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
    @Scheduled(cron = "0 55 2 * * *") // Executa todos os dias às 02:30
    public void processarRendimentosDiarios() {
        log.info("Iniciando processamento de rendimentos diários: {}", LocalDateTime.now());

        try {
            // Determina se hoje será lucro ou prejuízo para todos
            boolean isLucro = Math.random() < probabilidadeLucro;

            // Calcula o percentual do dia
            double percentualDoDia = isLucro ?
                    percentualMinLucro + (Math.random() * (percentualMaxLucro - percentualMinLucro)) :
                    percentualPrejuizo;

            log.info("Resultado do dia: {} - Percentual: {:.2f}%",
                    isLucro ? "LUCRO" : "PREJUÍZO",
                    percentualDoDia);

            // Busca todos os usuários ativos com saldo investido
            List<User> usuarios = userRepository.findByIsActiveTrueAndSaldoInvestidoGreaterThan(BigDecimal.ZERO);

            for (User usuario : usuarios) {
                processarRendimentoUsuario(usuario, BigDecimal.valueOf(percentualDoDia));
            }

            log.info("Processamento de rendimentos concluído com sucesso.");

        } catch (Exception e) {
            log.error("Erro ao processar rendimentos diários: " + e.getMessage(), e);
            throw new RuntimeException("Erro no processamento de rendimentos", e);
        }
    }

    @Transactional
    public void processarRendimentoUsuario(User usuario, BigDecimal percentualDoDia) {
        try {
            BigDecimal saldoInvestido = usuario.getSaldoInvestido();
            BigDecimal valorRendimento = saldoInvestido.multiply(percentualDoDia)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Cria ou busca o investimento ativo do usuário
            Investimento investimento = buscarOuCriarInvestimentoAtivo(usuario);

            // Registra o rendimento principal
            Rendimento rendimento = new Rendimento();
            rendimento.setUsuario(usuario);
            rendimento.setInvestimento(investimento); // Vincula o investimento
            rendimento.setValorRendimento(valorRendimento);
            rendimento.setTipoRendimento("I");
            rendimento.setPercentualRendimento(percentualDoDia);
            rendimentoRepository.save(rendimento);

            // Atualiza saldo do usuário
            usuario.setSaldoRendimentos(usuario.getSaldoRendimentos().add(valorRendimento));
            userRepository.save(usuario);

            // Processa rendimentos das indicações
            processarRendimentosIndicacoes(usuario, valorRendimento, investimento);

        } catch (Exception e) {
            log.error("Erro ao processar rendimento para usuário {}: {}", usuario.getId(), e.getMessage());
            throw e;
        }
    }

    private Investimento buscarOuCriarInvestimentoAtivo(User usuario) {
        return investimentoRepository.findFirstByUsuarioAndStatusOrderByDataInicioDesc(usuario, "A")
                .orElseGet(() -> {
                    Investimento novoInvestimento = new Investimento();
                    novoInvestimento.setUsuario(usuario);
                    novoInvestimento.setValorInvestido(usuario.getSaldoInvestido());
                    novoInvestimento.setStatus("A"); // Ativo
                    novoInvestimento.setDataInicio(LocalDateTime.now());
                    return investimentoRepository.save(novoInvestimento);
                });
    }

    private void processarRendimentosIndicacoes(User usuario, BigDecimal valorRendimentoPrincipal, Investimento investimento) {
        List<Indicacao> indicacoes = indicacaoRepository.findByUsuarioIndicadorAndIsActiveTrue(usuario);

        for (Indicacao indicacao : indicacoes) {
            NivelIndicacao nivelIndicacao = nivelIndicacaoRepository
                    .findByNivelAndIsActiveTrue(indicacao.getNivel())
                    .orElseThrow(() -> new RuntimeException("Nível de indicação não encontrado"));

            BigDecimal percentualNivel = nivelIndicacao.getPercentualRendimento();
            BigDecimal valorRendimentoIndicacao = valorRendimentoPrincipal
                    .multiply(percentualNivel)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            Rendimento rendimentoIndicacao = new Rendimento();
            rendimentoIndicacao.setUsuario(indicacao.getUsuarioIndicador());
            rendimentoIndicacao.setInvestimento(investimento); // Vincula o mesmo investimento
            rendimentoIndicacao.setValorRendimento(valorRendimentoIndicacao);
            rendimentoIndicacao.setTipoRendimento("N" + indicacao.getNivel());
            rendimentoIndicacao.setPercentualRendimento(percentualNivel);
            rendimentoRepository.save(rendimentoIndicacao);

            User indicador = indicacao.getUsuarioIndicador();
            indicador.setSaldoRendimentos(indicador.getSaldoRendimentos().add(valorRendimentoIndicacao));
            userRepository.save(indicador);
        }
    }
}

