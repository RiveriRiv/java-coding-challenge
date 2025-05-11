package com.crewmeister.cmcodingchallenge.currency.rates.repository;

import com.crewmeister.cmcodingchallenge.currency.rates.entity.CurrencyConversionRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyConversionRateRepository extends JpaRepository<CurrencyConversionRate, Long> {

    List<CurrencyConversionRate> findByTargetCurrency(String targetCurrency);

    Page<CurrencyConversionRate> findByTargetCurrencyIgnoreCase(String currency, Pageable pageable);

    Optional<CurrencyConversionRate> findByTargetCurrencyAndDate(String targetCurrency, LocalDate date);

    @Query("SELECT MAX(e.date) FROM CurrencyConversionRate e")
    Optional<LocalDate> findMaxDate();
}
