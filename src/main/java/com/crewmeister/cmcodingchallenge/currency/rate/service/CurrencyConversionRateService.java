package com.crewmeister.cmcodingchallenge.currency.rate.service;

import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CurrencyConversionRateService {

    Page<CurrencyConversionRate> getPageableCurrencyRatesForAllDates(String currency, Pageable pageable);

    CurrencyConversionRate getCurrencyRateForParticularDate(String currency, LocalDate localDate);

    BigDecimal convertToEur(String currency, BigDecimal amount, LocalDate date);

    void syncRates();
}
