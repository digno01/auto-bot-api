package br.com.auto.bot.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/dados")
@Slf4j
public class ConsultaDadosController {

    @Autowired
    private RestTemplate restTemplate;  // Injeção do RestTemplate

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<String> obterInformacoesPorCpf(@PathVariable String cpf) {
        // URL da API externa com CPF como parâmetro
        String url = "http://ec2-54-233-242-121.sa-east-1.compute.amazonaws.com:3000/cpf/" + cpf;

        try {
            // Realiza a requisição GET com o CPF
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Logando a resposta da API externa
            log.info("Resposta da API externa para CPF {}: {}", cpf, response.getBody());

            // Retorna a resposta da API externa
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Loga o erro e retorna uma mensagem de erro
            log.error("Erro ao chamar API externa para CPF {}: {}", cpf, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
