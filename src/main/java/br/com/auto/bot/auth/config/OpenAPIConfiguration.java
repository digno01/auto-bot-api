package br.com.auto.bot.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Value("${url.api.ambiente}")
    private String ambiente;

    @Value("${url.api.contato.nome}")
    private String nome;

    @Value("${url.api.contato.email}")
    private String email;

    @Value("${url.api.version}")
    private String version;

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("");
        server.setDescription(ambiente);

        Contact myContact = new Contact();
        myContact.setName(nome);
        myContact.setEmail(email);

        Info information = new Info()
                .title("Auth Rest API")
                .version(version)
                .description("APIs para cadastro de usuários")
                .contact(myContact);

        // Esquema de autenticação do tipo bearer
        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .type(Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("bearerAuth")
                .in(In.HEADER);


        return new OpenAPI()
                .info(information)
                .servers(List.of(server))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", bearerAuthScheme))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));
    }
}
