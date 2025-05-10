package com.crewmeister.cmcodingchallenge.currency.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rates")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CurrencyConversionRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private double conversionRate;

    @NonNull
    @Column(nullable = false)
    private String baseCurrency;

    @NonNull
    @Column(nullable = false)
    private String targetCurrency;

    @NonNull
    @Column(nullable = false)
    private LocalDate date;
}
