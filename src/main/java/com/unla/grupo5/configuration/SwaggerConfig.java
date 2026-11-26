package com.unla.grupo5.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Epicentro Gourmet - API REST").version("1.0").description("Documentación de los endpoints REST para el Hito 3 de Orientación a Objetos II - Grupo 05"));
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder().group("epicentro-gourmet-api").packagesToScan("com.unla.grupo5.controllers.api").build();
    }
}