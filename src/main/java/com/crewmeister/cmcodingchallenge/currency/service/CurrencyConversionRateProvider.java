package com.crewmeister.cmcodingchallenge.currency.service;

import java.time.LocalDate;

public interface CurrencyConversionRateProvider {
    void getEurFxRatesSince(LocalDate fromDate, String currencyCode);
}
