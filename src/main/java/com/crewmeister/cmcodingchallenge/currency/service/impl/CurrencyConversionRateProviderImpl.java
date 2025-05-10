package com.crewmeister.cmcodingchallenge.currency.service.impl;

import com.crewmeister.cmcodingchallenge.currency.config.CurrencyConversionRateCsvProperties;
import com.crewmeister.cmcodingchallenge.currency.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyConversionRateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConversionRateProviderImpl implements CurrencyConversionRateProvider {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final CurrencyConversionRateCsvProperties properties;
    private final CurrencyConversionRateRepository repository;

    @Override
    public void getEurFxRatesSince(LocalDate fromDate, String currencyCode) {
        List<CurrencyConversionRate> allRates = fetchAllRatesFromBundesbank(currencyCode);

        List<CurrencyConversionRate> rates = allRates.stream()
                .filter(rate -> !rate.getDate().isBefore(fromDate))
                .collect(Collectors.toList());

        repository.saveAll(rates);
    }

    private List<CurrencyConversionRate> fetchAllRatesFromBundesbank(String currency) {
        try {
            Resource resource = new UrlResource(URI.create(properties.getUrl().replace("{currency}", currency)));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                return reader.lines()
                        .skip(1)
                        .map(line -> parseCsvLine(line, currency))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            log.error("Could not parse csv for a currency={}: {}", currency, e.getMessage());
            return List.of();
        }
    }

    private CurrencyConversionRate parseCsvLine(String line, String targetCurrency) {
        try {
            String[] parts = line.replace("\"", "").split(",");
            if (parts.length < 2) {
                return null;
            }

            LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);
            double rate = Double.parseDouble(parts[1]);

            return new CurrencyConversionRate(rate, "EUR", targetCurrency, date);
        } catch (Exception e) {
            log.error("Failed to parse CSV line: {}", line, e);
            return null;
        }
    }
}
