package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.entity.CurrencyConversionRate;

import java.util.List;

public interface CurrencyConversionRateService {
    List<CurrencyConversionRate> getCurrencyRatesForAllDates(String currency);

    void syncRates();
}
