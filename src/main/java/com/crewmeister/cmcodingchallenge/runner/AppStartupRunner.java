package com.crewmeister.cmcodingchallenge.runner;

import com.crewmeister.cmcodingchallenge.currency.loader.CurrencyCsvLoader;
import com.crewmeister.cmcodingchallenge.currency.rate.service.CurrencyConversionRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class AppStartupRunner implements ApplicationRunner {

    private final CurrencyCsvLoader loader;

    private final CurrencyConversionRateService syncService;

    @Override
    public void run(ApplicationArguments args) {
        loader.loadCurrencies();
        syncService.syncRates();
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledSync() {
        syncService.syncRates();
    }
}
