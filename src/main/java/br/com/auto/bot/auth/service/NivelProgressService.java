package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.NivelRequirementProgressDTO;
import br.com.auto.bot.auth.dto.ProgressoNiveisDTO;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.UserRepository;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class NivelProgressService {
    private final Map<Integer, NivelConfig> niveisConfig;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvestimentoService investimentoService;


    public NivelProgressService() {
        niveisConfig = new HashMap<>();
        niveisConfig.put(1, new NivelConfig(5, 0, BigDecimal.ZERO));
        niveisConfig.put(2, new NivelConfig(7, 24, new BigDecimal("1200")));
        niveisConfig.put(3, new NivelConfig(9, 48, new BigDecimal("2400")));
        niveisConfig.put(4, new NivelConfig(11, 64, new BigDecimal("7200")));
        niveisConfig.put(5, new NivelConfig(15, 120, new BigDecimal("19200")));
    }

    public ProgressoNiveisDTO calcularProgressoTodosNiveis() {
        User usuario = userRepository.findById(ObterDadosUsuarioLogado.getUsuarioLogadoId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        ProgressoNiveisDTO progresso = new ProgressoNiveisDTO();
        Map<Integer, NivelRequirementProgressDTO> progressoPorNivel = new HashMap<>();

        // Obter dados atuais do usuário
        Integer investimentosAtuais = investimentoService.calcularQuantidadeInvestimentos();
        Integer convitesAtuais = usuario.getQtdIndicacoesDiretas() + usuario.getQtdIndicacoesIndiretas();

        BigDecimal depositoAtual = investimentoService.sumValorPixPagosByUsuarioId();

        // Calcular progresso para cada nível
        for (int nivel = 1; nivel <= 5; nivel++) {
            NivelConfig config = niveisConfig.get(nivel);
            NivelRequirementProgressDTO nivelProgresso = new NivelRequirementProgressDTO();

            // Investimentos
            nivelProgresso.setInvestimentosAtuais(investimentosAtuais);
            nivelProgresso.setInvestimentosNecessarios(config.getInvestimentosMinimos());
            nivelProgresso.setPercentualInvestimentos(
                    calcularPercentual(investimentosAtuais.doubleValue(), config.getInvestimentosMinimos().doubleValue())
            );

            // Convites
            nivelProgresso.setConvitesAtuais(convitesAtuais);
            nivelProgresso.setConvitesNecessarios(config.getConvitesMinimos());
            nivelProgresso.setPercentualConvites(
                    calcularPercentual(convitesAtuais.doubleValue(), config.getConvitesMinimos().doubleValue())
            );

            // Depósitos
            nivelProgresso.setDepositoAtual(depositoAtual);
            nivelProgresso.setDepositoNecessario(config.getDepositoMinimo());
            nivelProgresso.setPercentualDeposito(
                    calcularPercentual(depositoAtual.doubleValue(), config.getDepositoMinimo().doubleValue())
            );

            // Calcular percentual total do nível
            double percentualTotal = (nivelProgresso.getPercentualInvestimentos() +
                    nivelProgresso.getPercentualConvites() +
                    nivelProgresso.getPercentualDeposito()) / 3.0;
            nivelProgresso.setPercentualTotal(percentualTotal);

            // Verificar se o nível foi atingido
            nivelProgresso.setNivelAtingido(
                    investimentosAtuais >= config.getInvestimentosMinimos() &&
                            convitesAtuais >= config.getConvitesMinimos() &&
                            depositoAtual.compareTo(config.getDepositoMinimo()) >= 0
            );

            progressoPorNivel.put(nivel, nivelProgresso);

            // Atualizar nível atual
            if (nivelProgresso.getNivelAtingido()) {
                progresso.setNivelAtual(nivel);
            }
        }
        progresso.setNivelAtual(usuario.getNivelConta());
        progresso.setProgressoPorNivel(progressoPorNivel);
        return progresso;
    }

    private Double calcularPercentual(Double atual, Double necessario) {
        if (necessario == 0) return 100.0;
        if (atual >= necessario) return 100.0;
        return (atual / necessario) * 100.0;
    }

    @Data
    @AllArgsConstructor
    private static class NivelConfig {
        private Integer investimentosMinimos;
        private Integer convitesMinimos;
        private BigDecimal depositoMinimo;
    }
}