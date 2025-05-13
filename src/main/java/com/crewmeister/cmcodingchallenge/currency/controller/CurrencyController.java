package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.entity.Currency;
import com.crewmeister.cmcodingchallenge.currency.rate.dto.ConversionResponse;
import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.service.CurrencyConversionRateService;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@OpenAPIDefinition(info = @Info(title = "My API", version = "v1", description = "API for demo"))
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    private final CurrencyConversionRateService currencyConversionRateService;

    @GetMapping("/currencies/all")
    @Operation(summary = "Get all currencies", description = "Fetches the list of all available currencies.")
    public ResponseEntity<List<Currency>> getCurrencies() {
        return new ResponseEntity<>(currencyService.getAllCurrencies(), HttpStatus.OK);
    }

    @Operation(summary = "Get pageable currency rates for all dates", description = "Fetches paginated exchange rates for a given currency.")
    @GetMapping("/currencies/{currency}/rates")
    public ResponseEntity<Page<CurrencyConversionRate>> getPageableCurrencyRatesForAllDates(
            @PathVariable
            @Parameter(description = "Currency code", example = "USD") String currency,
            @ParameterObject
            @PageableDefault(page = 1, size = 50, sort = "date") Pageable pageable) {
        return ResponseEntity.ok(currencyConversionRateService.getPageableCurrencyRatesForAllDates(currency, pageable));
    }

    @Operation(summary = "Get currency rate for a particular date", description = "Fetches the exchange rate for a given currency on a specific date.")
    @GetMapping("/currencies/{currency}/rates/{date}")
    public ResponseEntity<CurrencyConversionRate> getCurrencyRateForParticularDate(
            @PathVariable
            @Parameter(description = "Currency code", example = "USD") String currency,
            @PathVariable
            @Parameter(description = "Date", example = "2020-11-12")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(currencyConversionRateService.getCurrencyRateForParticularDate(currency, date), HttpStatus.OK);
    }

    @Operation(summary = "Convert to EUR", description = "Converts a given amount of foreign currency to EUR based on the exchange rate for a specific date.")
    @GetMapping("/currencies/{currency}/convert")
    public ResponseEntity<ConversionResponse> convertToEur(
            @PathVariable
            @Parameter(description = "Currency code", example = "USD") String currency,
            @RequestParam
            @Parameter(description = "Amount", example = "100.0") BigDecimal amount,
            @RequestParam
            @Parameter(description = "Date", example = "2021-08-10")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

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
