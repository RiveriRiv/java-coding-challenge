package com.crewmeister.cmcodingchallenge.currency.rate.loader;

import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;

import java.util.List;

public interface CurrencyConversionBatchService {
    void saveBatch(List<CurrencyConversionRate> batch);
}
