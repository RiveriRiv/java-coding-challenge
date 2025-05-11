package com.crewmeister.cmcodingchallenge.currency.rate.loader;

import java.time.LocalDate;

public interface CurrencyConversionRateLoader {
    void getEurFxRatesSince(LocalDate fromDate, String currencyCode);
}
