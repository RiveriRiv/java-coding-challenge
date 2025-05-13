package com.crewmeister.cmcodingchallenge.currency.loader.impl;

import com.crewmeister.cmcodingchallenge.currency.config.CurrencyCsvProperties;
import com.crewmeister.cmcodingchallenge.currency.entity.Currency;
import com.crewmeister.cmcodingchallenge.currency.loader.CurrencyCsvLoader;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyRepository;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyCsvLoaderImpl implements CurrencyCsvLoader {

    private final CurrencyRepository currencyRepository;
    private final CurrencyCsvProperties properties;

    @Override
    public void loadCurrencies() {
        try {

            Resource resource = new UrlResource(URI.create(properties.getUrl()));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                reader.lines()
                        .map(this::parseCsvLine)
                        .flatMap(List::stream)
                        .forEach(currencyRepository::save);
            }
        } catch (IOException e) {
            log.error("Failed to load currencies {}", e.getLocalizedMessage());
        }
    }

    private List<Currency> parseCsvLine(String line) {
        try {
            Pattern pattern = Pattern.compile("D\\.([A-Z]{3})\\.EUR");
            Matcher matcher;

            List<Currency> currencies = new ArrayList<>();

            String[] seriesArray = line.split(",");
            for (String series : seriesArray) {
                if (!series.endsWith("_FLAGS")) {
                    matcher = pattern.matcher(series);
                    if (matcher.find()) {
                        currencies.add(new Currency(matcher.group(1)));
                    }
                }
            }

            return currencies;
        } catch (Exception e) {
            log.error("Failed to parse CSV line: {}", line, e);
            return List.of();
        }
    }
}