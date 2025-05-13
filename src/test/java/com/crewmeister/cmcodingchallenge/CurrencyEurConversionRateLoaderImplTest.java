package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.loader.impl.CurrencyEurConversionRateLoaderImpl;
import com.crewmeister.cmcodingchallenge.currency.rate.repository.CurrencyConversionRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "currencies.csv.url=file:src/test/resources/test-usd.csv"
})
@Transactional
class CurrencyEurConversionRateLoaderImplTest {

    @Autowired
    private CurrencyEurConversionRateLoaderImpl loader;

    @Autowired
    private CurrencyConversionRateRepository repository;

    @Test
    void shouldLoadCurrencyConversionRatesFromCsv() {
        loader.loadCurrencyConversionRatesSince(LocalDate.of(2000, 1, 1), "USD");

        List<CurrencyConversionRate> allRates = repository.findAll();
        assertThat(allRates).isNotEmpty();

        CurrencyConversionRate first = allRates.get(0);
        assertThat(first.getBaseCurrency()).isEqualTo("EUR");
        assertThat(first.getTargetCurrency()).isEqualTo("USD");
        assertThat(first.getConversionRate()).isGreaterThan(0);
    }
}
