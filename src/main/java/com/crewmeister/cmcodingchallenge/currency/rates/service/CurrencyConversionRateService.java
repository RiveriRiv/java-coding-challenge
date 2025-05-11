package com.crewmeister.cmcodingchallenge.currency.rates.service;

import com.crewmeister.cmcodingchallenge.currency.rates.entity.CurrencyConversionRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyConversionRateService {
    List<CurrencyConversionRate> getCurrencyRatesForAllDates(String currency);

    Page<CurrencyConversionRate> getPageableCurrencyRatesForAllDates(String currency, Pageable pageable);

    CurrencyConversionRate getCurrencyRateForParticularDate(String currency, LocalDate localDate);

    void syncRates();
}
