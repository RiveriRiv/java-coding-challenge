package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.currency.rates.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyRateNotFoundException;
import com.crewmeister.cmcodingchallenge.currency.rates.repository.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.rates.service.impl.CurrencyConversionRateServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CurrencyConversionRateServiceImplTest {

    @Autowired
    private CurrencyConversionRateServiceImpl service;

    @MockBean
    private CurrencyConversionRateRepository repository;

    @Test
    void whenCurrencyRateExists_thenReturnIt() {
        // given
        String currency = "USD";
        LocalDate date = LocalDate.of(2023, 5, 10);
        CurrencyConversionRate rate = new CurrencyConversionRate(1.1, "EUR", currency, date);

        when(repository.findByTargetCurrencyAndDate(currency, date))
                .thenReturn(Optional.of(rate));

        // when
        CurrencyConversionRate result = service.getCurrencyRateForParticularDate(currency, date);

        // then
        assertThat(result).isEqualTo(rate);
        verify(repository, times(1)).findByTargetCurrencyAndDate(currency, date);
    }

    @Test
    void whenCurrencyRateDoesNotExist_thenThrowException() {
        // given
        String currency = "USD";
        LocalDate date = LocalDate.of(2023, 5, 10);

        // when
        when(repository.findByTargetCurrencyAndDate(currency, date))
                .thenReturn(Optional.empty());

        // then
        assertThrows(CurrencyRateNotFoundException.class, () ->
                service.getCurrencyRateForParticularDate(currency, date));

        verify(repository, times(1)).findByTargetCurrencyAndDate(currency, date);
    }
}

