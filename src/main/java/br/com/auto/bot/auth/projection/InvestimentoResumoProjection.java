package br.com.auto.bot.auth.projection;

import java.math.BigDecimal;

public interface InvestimentoResumoProjection {
    String getNomeRobo();
    Long getQuantidadeInvestimentos();
    BigDecimal getValorTotalInvestido();
    BigDecimal getMediaPercentualRendimento();
}
