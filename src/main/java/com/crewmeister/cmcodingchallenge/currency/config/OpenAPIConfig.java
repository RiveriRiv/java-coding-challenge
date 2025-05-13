package com.crewmeister.cmcodingchallenge.currency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class OpenAPIConfig {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customizePageable() {
        return resolver -> {
            resolver.setMaxPageSize(100);
            resolver.setOneIndexedParameters(true);
            resolver.setFallbackPageable(PageRequest.of(0, 20));
        };
    }
}

