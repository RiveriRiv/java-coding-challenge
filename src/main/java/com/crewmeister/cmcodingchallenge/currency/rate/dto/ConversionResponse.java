package com.crewmeister.cmcodingchallenge.currency.rate.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ConversionResponse {
    private String fromCurrency;
    private String toCurrency;
    private LocalDate date;
    private BigDecimal originalAmount;
    private BigDecimal convertedAmount;
}
