package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.entity.Currency;

import java.util.List;

public interface CurrencyService {

    List<Currency> getAllCurrencies();

    void saveAllCurrencies(List<Currency> currencies);
}
