package com.crewmeister.cmcodingchallenge.currency.repository;

import com.crewmeister.cmcodingchallenge.currency.entity.CurrencyConversionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyConversionRateRepository extends JpaRepository<CurrencyConversionRate, Long> {

    List<CurrencyConversionRate> findByTargetCurrency(String targetCurrency);

    @Query("SELECT MAX(e.date) FROM CurrencyConversionRate e")
    Optional<LocalDate> findMaxDate();
}
