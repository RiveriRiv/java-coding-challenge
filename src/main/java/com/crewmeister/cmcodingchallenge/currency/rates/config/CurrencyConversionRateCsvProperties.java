package com.crewmeister.cmcodingchallenge.currency.rates.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "exchange.csv")
@Data
public class CurrencyConversionRateCsvProperties {
    private String url;
}
