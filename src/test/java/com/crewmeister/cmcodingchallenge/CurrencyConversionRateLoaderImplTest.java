package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.repository.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.rate.loader.impl.CurrencyConversionRateLoaderImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {
        "exchange.csv.url=file:src/test/resources/test-{currency}.csv"
})
@ActiveProfiles("test")
class CurrencyConversionRateLoaderImplTest {

    @Autowired
    private CurrencyConversionRateLoaderImpl provider;

    @MockBean
    private CurrencyConversionRateRepository repository;

    @Test
    void shouldLoadAndSaveRatesFromCsvFile() {
        // given
        LocalDate fromDate = LocalDate.of(2024, 5, 2);
        String currencyCode = "USD";

        // when
        provider.getEurFxRatesSince(fromDate, currencyCode);

        // then
        ArgumentCaptor<List<CurrencyConversionRate>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<CurrencyConversionRate> savedRates = captor.getValue();
        assertThat(savedRates).hasSize(1);

        CurrencyConversionRate rate = savedRates.get(0);
        assertThat(rate.getTargetCurrency()).isEqualTo("USD");
        assertThat(rate.getBaseCurrency()).isEqualTo("EUR");
        assertThat(rate.getDate()).isEqualTo(LocalDate.of(2024, 5, 5));
        assertThat(rate.getConversionRate()).isEqualTo(1.090);
    }
}
