package com.mauriexchange.code.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MauriExchange Currency API")
                        .description("REST API for managing currency exchange data. " +
                                   "This API provides endpoints to fetch currency information " +
                                   "and exchange rates from the local JSON data source.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MauriExchange Team")
                                .email("support@mauriexchange.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.mauriexchange.com")
                                .description("Production Server")
                ));
    }
}
