package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.currency.entity.Currency;
import com.crewmeister.cmcodingchallenge.currency.loader.impl.CurrencyCsvLoaderImpl;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "currencies.csv.url=file:src/test/resources/test-currencies.csv"
})
@Transactional
class CurrencyCsvLoaderImplTest {

    @Autowired
    private CurrencyCsvLoaderImpl loader;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    void shouldLoadCurrenciesFromCsv() {
        loader.loadCurrencies();

        List<Currency> allCurrencies = currencyRepository.findAll();
        assertThat(allCurrencies).isNotEmpty();
        assertThat(allCurrencies).extracting(Currency::getCode).contains("BGN", "AUD");
    }
}
