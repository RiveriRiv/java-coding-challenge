package com.crewmeister.cmcodingchallenge.currency.rate.loader.impl;

import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.loader.CurrencyConversionBatchService;
import com.crewmeister.cmcodingchallenge.currency.rate.repository.CurrencyConversionRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyConversionBatchServiceImpl implements CurrencyConversionBatchService {
    private final CurrencyConversionRateRepository repository;

    @Transactional
    public void saveBatch(List<CurrencyConversionRate> batch) {
        repository.saveAll(batch);
    }
}
