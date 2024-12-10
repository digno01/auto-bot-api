package br.com.auto.bot.auth.service;


import br.com.auto.bot.auth.dto.ReferralAnalyticsDTO;
import br.com.auto.bot.auth.exceptions.BusinessException;
import br.com.auto.bot.auth.model.Indicacao;
import br.com.auto.bot.auth.model.Investimento;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.repository.IndicacaoRepository;
import br.com.auto.bot.auth.repository.InvestimentoRepository;
import br.com.auto.bot.auth.repository.UserRepository;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferralAnalyticsService {

    private final IndicacaoRepository indicacaoRepository;
    private final InvestimentoRepository investimentoRepository;
    private final UserRepository userRepository;

    public List<ReferralAnalyticsDTO> getReferralAnalytics() {
        User usuario = userRepository.findById(ObterDadosUsuarioLogado.getUsuarioLogadoId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));


        List<Indicacao> referrals = indicacaoRepository.findByUsuarioIndicador(usuario);

        return referrals.stream()
                .map(referral -> {
                    User referredUser = referral.getUsuario();
                    List<Investimento> investments = investimentoRepository.findByUsuario(referredUser);

                    // Filter valid investments (valorEfetuadoPIX > 0)
                    List<Investimento> validInvestments = investments.stream()
                            .filter(inv -> inv.getValorEfetuadoPIX() != null &&
                                    inv.getValorEfetuadoPIX().compareTo(BigDecimal.ZERO) > 0)
                            .collect(Collectors.toList());

                    // Calculate total PIX value
                    BigDecimal totalPixValue = validInvestments.stream()
                            .map(Investimento::getValorEfetuadoPIX)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Get first PIX value
                    BigDecimal firstPixValue = validInvestments.isEmpty() ?
                            BigDecimal.ZERO :
                            validInvestments.get(0).getValorEfetuadoPIX();

                    return ReferralAnalyticsDTO.builder()
                            .userName(referredUser.getNome())
                            .level(referral.getUsuario().getNivelConta())
                            .totalPixValue(totalPixValue)
                            .firstPixValue(firstPixValue)
                            .mylevel(usuario.getNivelConta())
                            .totalInvestments(validInvestments.size())
                            .build();
                })
                .collect(Collectors.toList());
    }
}