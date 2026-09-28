package com.library.library_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {


    @Bean
    public OpenAPI libraryOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Library Management System API")
                                .description(
                                        "REST API for Library Management System"
                                )
                                .version("1.0")
                );
    }
}