package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.entity.Currency;
import com.crewmeister.cmcodingchallenge.currency.rate.dto.ConversionResponse;
import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.service.CurrencyConversionRateService;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController()
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    private final CurrencyConversionRateService currencyConversionRateService;

    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getCurrencies() {
        return new ResponseEntity<>(currencyService.getAllCurrencies(), HttpStatus.OK);
    }

    @GetMapping("/currencies/{currency}/rates/all")
    public ResponseEntity<List<CurrencyConversionRate>> getCurrencyRatesForAllDates(@PathVariable String currency) {
        return ResponseEntity.ok(currencyConversionRateService.getCurrencyRatesForAllDates(currency));
    }

    @GetMapping("/currencies/{currency}/rates")
    public ResponseEntity<Page<CurrencyConversionRate>> getPageableCurrencyRatesForAllDates(
            @PathVariable String currency,
            Pageable pageable) {
        return ResponseEntity.ok(currencyConversionRateService.getPageableCurrencyRatesForAllDates(currency, pageable));
    }

    @GetMapping("/currencies/{currency}/rates/{date}")
    public ResponseEntity<CurrencyConversionRate> getCurrencyRateForParticularDate(@PathVariable String currency,
                                                                                   @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(currencyConversionRateService.getCurrencyRateForParticularDate(currency, date), HttpStatus.OK);
    }

    @GetMapping("/currencies/{currency}/convert")
    public ResponseEntity<ConversionResponse> convertToEur(
            @PathVariable String currency,
            @RequestParam BigDecimal amount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        BigDecimal convertedAmount = currencyConversionRateService.convertToEur(currency, amount, date);

        ConversionResponse response = new ConversionResponse(
                currency,
                "EUR",
                date,
                amount,
                convertedAmount
        );

        return ResponseEntity.ok(response);
    }
}
