package br.com.auto.bot.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralAnalyticsDTO {
    private String userName;
    private Integer level;
    private Integer mylevel;
    private BigDecimal totalPixValue;
    private BigDecimal firstPixValue;
    private Integer totalInvestments;
}