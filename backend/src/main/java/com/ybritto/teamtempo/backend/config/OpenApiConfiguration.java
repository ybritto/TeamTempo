package com.ybritto.teamtempo.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TeamTempo App")
                        .description("An application where team planning find its tempo")
                        .version("v1")
                        .contact(new Contact()
                                .name("TeamTempo Team")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("URL for local development")
                ));
    }
}
