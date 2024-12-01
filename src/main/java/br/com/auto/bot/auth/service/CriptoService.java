package br.com.auto.bot.auth.service;

import br.com.auto.bot.auth.dto.CriptoData;
import br.com.auto.bot.auth.exceptions.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CriptoService {
    private static final String COINGECKO_API = "https://api.coingecko.com/api/v3";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CriptoService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    private String extrairCaminhoImagem(String urlCompleta) {
        try {
            if (urlCompleta == null || urlCompleta.isEmpty()) {
                return null;
            }

            // Procura o padrão "coins/images/" e pega tudo que vem depois
            int index = urlCompleta.indexOf("coins/images/");
            if (index != -1) {
                return urlCompleta.substring(index + "coins/images/".length());
            }

            return null;
        } catch (Exception e) {
            log.warn("Erro ao extrair caminho da imagem da URL: {}", urlCompleta);
            return null;
        }
    }



    public List<CriptoData> buscarDadosCripto24h() {
        try {
            String url = COINGECKO_API + "/coins/markets" +
                    "?vs_currency=usd" +
                    "&order=market_cap_desc" +
                    "&per_page=100" +
                    "&page=1" +
                    "&sparkline=false" +
                    "&price_change_percentage=24h";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            List<CriptoData> criptos = new ArrayList<>();

            root.forEach(node -> {
                CriptoData cripto = new CriptoData();
                cripto.setId(node.get("id").asText());
                cripto.setSymbol(node.get("symbol").asText().toUpperCase());
                cripto.setCurrentPrice(new BigDecimal(node.get("current_price").asText()));
                cripto.setImage(extrairCaminhoImagem(node.get("image").asText()));
                cripto.setPriceChangePercentage24h(new BigDecimal(node.get("price_change_percentage_24h").asText()));
                cripto.setLow24h(new BigDecimal(node.get("low_24h").asText()));
                cripto.setHigh24h(new BigDecimal(node.get("high_24h").asText()));
                criptos.add(cripto);
            });

            return criptos.stream()
                    .filter(c -> c.getPriceChangePercentage24h().compareTo(BigDecimal.ZERO) > 0)
                    .sorted(Comparator.comparing(CriptoData::getPriceChangePercentage24h).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Erro ao buscar dados de criptomoedas: {}", e.getMessage());
            throw new BusinessException("Erro ao buscar dados de criptomoedas", e);
        }
    }
}
