package br.com.auto.bot.auth.service;
import br.com.auto.bot.auth.dto.NivelDTO;
import br.com.auto.bot.auth.dto.ProgressoNivelDTO;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.Indicacao;
import br.com.auto.bot.auth.enums.StatusInvestimento;

import br.com.auto.bot.auth.repository.UserRepository;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NivelService {

    private final List<NivelDTO> niveisConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvestimentoService investimentoService;
    public NivelService() {
        niveisConfig = new ArrayList<>();
        niveisConfig.add(new NivelDTO(1, 5, 0, BigDecimal.ZERO));
        niveisConfig.add(new NivelDTO(2, 7, 24, new BigDecimal("200")));
        niveisConfig.add(new NivelDTO(3, 9, 48, new BigDecimal("400")));
        niveisConfig.add(new NivelDTO(4, 11, 64, new BigDecimal("1200")));
        niveisConfig.add(new NivelDTO(5, 15, 120, new BigDecimal("3200")));
    }

    public ProgressoNivelDTO calcularProgressoNivel() {

        User usuario = userRepository.findById(ObterDadosUsuarioLogado.getUsuarioLogadoId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Cálculos básicos
        int qtdInvestimentos = calcularQuantidadeInvestimentos();
        int qtdConvites = usuario.getQtdIndicacoesDiretas();
        BigDecimal totalDepositado = calcularTotalDepositado();

        // Determinar nível atual e próximo nível
        int nivelAtual = determinarNivelAtual(qtdInvestimentos, qtdConvites, totalDepositado);
        NivelDTO nivelAtualConfig = niveisConfig.get(nivelAtual - 1);
        NivelDTO proximoNivelConfig = nivelAtual < 5 ? niveisConfig.get(nivelAtual) : null;

        // Calcular progressos
        ProgressoNivelDTO progresso = new ProgressoNivelDTO();
        progresso.setNivelAtual(nivelAtual);

        if (proximoNivelConfig != null) {
            // Progresso de investimentos
            double progressoInvestimentos = calcularPorcentagem(
                    qtdInvestimentos,
                    nivelAtualConfig.getInvestimentosMinimos(),
                    proximoNivelConfig.getInvestimentosMinimos()
            );

            // Progresso de convites
            double progressoConvites = calcularPorcentagem(
                    qtdConvites,
                    nivelAtualConfig.getConvitesMinimos(),
                    proximoNivelConfig.getConvitesMinimos()
            );

            // Progresso de depósitos
            double progressoDepositos = calcularPorcentagem(
                    totalDepositado.doubleValue(),
                    nivelAtualConfig.getDepositoMinimo().doubleValue(),
                    proximoNivelConfig.getDepositoMinimo().doubleValue()
            );

            progresso.setProgressoInvestimentos(progressoInvestimentos);
            progresso.setProgressoConvites(progressoConvites);
            progresso.setProgressoDepositos(progressoDepositos);

            // Progresso geral (média dos três)
            double progressoGeral = (progressoInvestimentos + progressoConvites + progressoDepositos) / 3;
            progresso.setProgressoGeral(progressoGeral);
        }

        // Adicionar dados atuais
        progresso.setQuantidadeInvestimentosAtual(qtdInvestimentos);
        progresso.setQuantidadeConvitesAtual(qtdConvites);
        progresso.setTotalDepositadoAtual(totalDepositado);

        return progresso;
    }

    private int calcularQuantidadeInvestimentos() {
        Integer qntd   = investimentoService.calcularQuantidadeInvestimentos();

        return qntd;
    }


    private BigDecimal calcularTotalDepositado() {
        BigDecimal valorTotalDepositado =  investimentoService.sumValorPixPagosByUsuarioId();
        return valorTotalDepositado;
    }

    private int determinarNivelAtual(int qtdInvestimentos, int qtdConvites, BigDecimal totalDepositado) {
        int nivelAtual = 0;
        for (NivelDTO nivel : niveisConfig) {
            if (qtdInvestimentos >= nivel.getInvestimentosMinimos() &&
                    qtdConvites >= nivel.getConvitesMinimos() &&
                    totalDepositado.compareTo(nivel.getDepositoMinimo()) >= 0) {
                nivelAtual = nivel.getNivel();
            } else {
                break;
            }
        }
        return Math.max(1, nivelAtual);
    }

    private double calcularPorcentagem(double atual, double minAtual, double minProximo) {
        if (atual <= minAtual) return 0.0;
        if (atual >= minProximo) return 100.0;

        double range = minProximo - minAtual;
        double progress = atual - minAtual;

        return (progress / range) * 100.0;
    }
}