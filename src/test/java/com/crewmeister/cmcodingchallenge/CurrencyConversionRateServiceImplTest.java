package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyRateNotFoundException;
import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.repository.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.rate.service.impl.CurrencyConversionRateServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void convertToEur_shouldReturnConvertedAmount() {
        // given
        BigDecimal amountInUsd = BigDecimal.valueOf(12.0);
        BigDecimal expectedAmountInEur = amountInUsd.divide(BigDecimal.valueOf(1.2), 6, BigDecimal.ROUND_HALF_UP);

        String currency = "USD";
        LocalDate date = LocalDate.of(2024, 5, 1);
        CurrencyConversionRate rate = new CurrencyConversionRate(1.2, "EUR", currency, date);

        // when
        when(repository.findByTargetCurrencyAndDate(currency, date))
                .thenReturn(Optional.of(rate));

        BigDecimal result = service.convertToEur(currency, amountInUsd, date);

        // then
        assertEquals(expectedAmountInEur, result);
    }

    @Test
    void convertToEur_shouldThrowCurrencyRateNotFoundException_whenRateIsMissing() {
        // given
        BigDecimal amountInUsd = BigDecimal.valueOf(12.0);
        String currency = "USD";
        LocalDate date = LocalDate.of(2024, 5, 1);

        // when
        when(repository.findByTargetCurrencyAndDate(currency, date))
                .thenReturn(Optional.empty());

        // then
        CurrencyRateNotFoundException exception = assertThrows(
                CurrencyRateNotFoundException.class,
                () -> service.convertToEur(currency, amountInUsd, date)
        );

        assertEquals("Currency rate not found for currency=USD and date=2024-05-01", exception.getMessage());
    }

    @Test
    void convertToEur_shouldThrowIllegalArgumentException_whenRateIsZero() {
        // given
        BigDecimal amountInUsd = BigDecimal.valueOf(12.0);
        String currency = "USD";
        LocalDate date = LocalDate.of(2024, 5, 1);

        CurrencyConversionRate rate = new CurrencyConversionRate(0.0, "EUR", currency, date);

        // when
        when(repository.findByTargetCurrencyAndDate(currency, date))
                .thenReturn(Optional.of(rate));

        // then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.convertToEur(currency, amountInUsd, date)
        );

        assertEquals("Exchange rate cannot be zero", exception.getMessage());
    }

}

