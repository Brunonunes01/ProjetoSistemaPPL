package com.projeto.Sistema.config;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {

    /**
     * Este método informa manualmente ao Spring para usar o
     * Thymeleaf Layout Dialect.
     * Isso corrige o problema do layout:decorate não funcionar.
     */
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}