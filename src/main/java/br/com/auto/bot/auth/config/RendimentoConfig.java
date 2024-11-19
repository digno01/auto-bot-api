package br.com.auto.bot.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "rendimento")
public class RendimentoConfig {
    private double percentualMinLucro = 5.0;
    private double percentualMaxLucro = 13.0;
    private double percentualPrejuizo = -3.0;
    private double probabilidadeLucro = 0.70; // 70%
    private double probabilidadePrejuizo = 0.30; // 30%
}
