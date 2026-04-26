package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coffeeShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coffee Shop MiniPOS API")
                        .description("""
                                REST API for the Coffee Shop Mini Point-of-Sale system.
                                Manage categories and sales with auto-generated codes.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Coffee Shop Dev Team")
                                .email("dev@coffeeshop.com"))
                        .license(new License()
                                .name("MIT License")));
    }
}
