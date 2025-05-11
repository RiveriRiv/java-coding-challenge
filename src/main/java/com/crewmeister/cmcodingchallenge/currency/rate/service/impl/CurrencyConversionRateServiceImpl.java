package com.crewmeister.cmcodingchallenge.currency.rate.service.impl;

import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyRateNotFoundException;
import com.crewmeister.cmcodingchallenge.currency.rate.repository.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.rate.loader.CurrencyConversionRateLoader;
import com.crewmeister.cmcodingchallenge.currency.rate.service.CurrencyConversionRateService;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConversionRateServiceImpl implements CurrencyConversionRateService {

    private final CurrencyConversionRateRepository repository;
    private final CurrencyConversionRateLoader provider;
    private final CurrencyService currencyService;

    @Override
    public List<CurrencyConversionRate> getCurrencyRatesForAllDates(String currency) {
        return repository.findByTargetCurrency(currency);
    }

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

    @Transactional
    @Override
    public void syncRates() {
        LocalDate lastDateInDb = repository.findMaxDate()
                .orElse(LocalDate.of(1999, 1, 1));

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<Future<Object>> tasks = currencyService.getAllCurrencies().stream()
                .map(currency -> executor.submit(() -> {
                    provider.getEurFxRatesSince(lastDateInDb.plusDays(1), currency.getCode());
                    return null;
                }))
                .collect(Collectors.toList());

        for (Future<Object> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Something went wrong: {}", e.getMessage());
            }
        }

        executor.shutdown();
    }
}
