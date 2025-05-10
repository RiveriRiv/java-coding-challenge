package com.crewmeister.cmcodingchallenge.runner;

import com.crewmeister.cmcodingchallenge.currency.loader.CurrencyCsvLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {

    private final CurrencyCsvLoader loader;

    @Override
    public void run(ApplicationArguments args) {
        loader.loadCurrencies();
    }
}
