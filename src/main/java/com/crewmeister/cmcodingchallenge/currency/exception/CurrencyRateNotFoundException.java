package com.crewmeister.cmcodingchallenge.currency.exception;

import java.time.LocalDate;

public class CurrencyRateNotFoundException extends RuntimeException {

    public CurrencyRateNotFoundException(String currency, LocalDate date) {
        super("Currency rate not found for currency=" + currency + " and date=" + date);
    }
}
