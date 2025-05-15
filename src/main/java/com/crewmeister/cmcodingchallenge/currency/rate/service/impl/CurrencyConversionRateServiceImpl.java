package com.crewmeister.cmcodingchallenge.currency.rate.service.impl;

import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyRateNotFoundException;
import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.loader.CurrencyConversionRateLoader;
import com.crewmeister.cmcodingchallenge.currency.rate.repository.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.rate.service.CurrencyConversionRateService;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConversionRateServiceImpl implements CurrencyConversionRateService {

    private final CurrencyConversionRateRepository repository;
    private final CurrencyConversionRateLoader provider;
    private final CurrencyService currencyService;

    @Override
    public Page<CurrencyConversionRate> getPageableCurrencyRatesForAllDates(String currency, Pageable pageable) {
        return repository.findByTargetCurrencyIgnoreCase(currency, pageable);
    }

    @Override
    public CurrencyConversionRate getCurrencyRateForParticularDate(String currency, LocalDate date) {
        return repository.findByTargetCurrencyAndDate(currency, date)
                .orElseThrow(() -> new CurrencyRateNotFoundException(currency, date));
    }

    @Override
    public BigDecimal convertToEur(String currency, BigDecimal amount, LocalDate date) {
        CurrencyConversionRate rate = repository.findByTargetCurrencyAndDate(currency, date)
                .orElseThrow(() -> new CurrencyRateNotFoundException(currency, date));

        if (rate.getConversionRate() == 0) {
            throw new IllegalArgumentException("Exchange rate cannot be zero");
        }

        return amount.divide(BigDecimal.valueOf(rate.getConversionRate()), 6, RoundingMode.HALF_UP);
    }

    @Override
    public void syncRates() {
        LocalDate lastDateInDb = repository.findMaxDate()
                .orElse(LocalDate.of(1999, 1, 1));

        ExecutorService executor = Executors.newFixedThreadPool(5);

        currencyService.getAllCurrencies().forEach(currency ->
                executor.execute(() -> {
                    try {
                        provider.loadCurrencyConversionRatesSince(lastDateInDb.plusDays(1), currency.getCode());
                    } catch (Exception e) {
                        log.error("Error loading rates for currency {}: {}", currency.getCode(), e.getMessage(), e);
                    }
                }));

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                log.warn("Executor did not terminate in the expected time.");
            }
        } catch (InterruptedException e) {
            log.error("Executor termination interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
