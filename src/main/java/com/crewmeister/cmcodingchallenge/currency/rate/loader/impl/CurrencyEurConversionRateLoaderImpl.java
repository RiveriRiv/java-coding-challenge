package com.crewmeister.cmcodingchallenge.currency.rate.loader.impl;

import com.crewmeister.cmcodingchallenge.currency.rate.config.CurrencyConversionRateCsvProperties;
import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.loader.CurrencyConversionRateLoader;
import com.crewmeister.cmcodingchallenge.currency.rate.repository.CurrencyConversionRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyEurConversionRateLoaderImpl implements CurrencyConversionRateLoader {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final CurrencyConversionRateCsvProperties properties;
    private final CurrencyConversionRateRepository repository;

    @Override
    public void loadCurrencyConversionRatesSince(LocalDate fromDate, String currencyCode) {
        fetchAndProcessRates(currencyCode);
    }

    private void fetchAndProcessRates(String currencyCode) {
        try {
            Path csvFile = downloadCsv(currencyCode);
            processCsvFile(csvFile, currencyCode);
            deleteTempFile(csvFile);
        } catch (IOException e) {
            log.error("Failed to process rates for currency={}: {}", currencyCode, e.getMessage(), e);
        }
    }

    private Path downloadCsv(String currencyCode) throws IOException {
        String downloadUrl = properties.getUrl().replace("{currency}", currencyCode);
        Resource resource = new UrlResource(URI.create(downloadUrl));

        Path tempFile = Files.createTempFile("currency_rates_" + currencyCode, ".csv");
        Files.copy(resource.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        log.info("Downloaded CSV for {} to {}", currencyCode, tempFile.toAbsolutePath());
        return tempFile;
    }

    private void processCsvFile(Path csvFile, String currencyCode) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvFile)) {
            int batchSize = 50;
            List<CurrencyConversionRate> batch = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                CurrencyConversionRate rate = parseCsvLine(line, currencyCode);
                if (rate != null) {
                    batch.add(rate);
                }

                if (batch.size() >= batchSize) {
                    saveBatch(batch);
                }
            }

            if (!batch.isEmpty()) {
                saveBatch(batch);
            }
        }
    }

    private void saveBatch(List<CurrencyConversionRate> batch) {
        repository.saveAll(batch);
        batch.clear();
    }

    private void deleteTempFile(Path tempFile) throws IOException {
        Files.deleteIfExists(tempFile);
        log.info("Deleted temp file {}", tempFile.toAbsolutePath());
    }

    private CurrencyConversionRate parseCsvLine(String line, String targetCurrency) {
        try {
            String[] parts = line.replace("\"", "").split(",");
            if (parts.length < 2) {
                return null;
            }

            LocalDate date = LocalDate.parse(parts[0], DATE_FORMATTER);

            if (isNotDoubleStringValue(parts[1])) {
                log.debug("Not a valid double: {}", parts[1]);
                return null;
            }

            double rate = Double.parseDouble(parts[1]);

            return new CurrencyConversionRate(rate, "EUR", targetCurrency, date);
        } catch (Exception e) {
            log.error("Failed to parse CSV line: {}", line, e);
            return null;
        }
    }

    private boolean isNotDoubleStringValue(String line) {
        return !line.matches("-?\\d+(\\.\\d+)?");
    }
}
