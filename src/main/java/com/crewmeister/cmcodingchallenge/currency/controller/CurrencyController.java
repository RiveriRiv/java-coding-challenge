package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.data.Currency;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/currencies")
    public ResponseEntity<List<Currency>> getCurrencies() {
        return new ResponseEntity<>(currencyService.getAllCurrencies(), HttpStatus.OK);
    }
}
