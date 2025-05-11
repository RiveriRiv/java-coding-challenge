package com.crewmeister.cmcodingchallenge.currency.exception.handler;

import com.crewmeister.cmcodingchallenge.currency.exception.CurrencyRateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CurrencyExceptionHandler {
    @ExceptionHandler(CurrencyRateNotFoundException.class)
    public ResponseEntity<String> handleCurrencyRateNotFound(CurrencyRateNotFoundException ex) {
        log.warn("Currency rate not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}
