package com.crewmeister.cmcodingchallenge.currency.loader.impl;

import com.crewmeister.cmcodingchallenge.currency.config.CurrencyCsvProperties;
import com.crewmeister.cmcodingchallenge.currency.entity.Currency;
import com.crewmeister.cmcodingchallenge.currency.loader.CurrencyCsvLoader;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyCsvLoaderImpl implements CurrencyCsvLoader {

    private final CurrencyService currencyService;
    private final CurrencyCsvProperties properties;

    private static final String TEMP_FILE_NAME = "currencies";
    private static final String TEMP_FILE_SUFFIX = ".xlsx";

    @Override
    public void loadCurrencies() {
        try {
            String url = properties.getUrl();
            Path tempFile = Files.createTempFile(TEMP_FILE_NAME, TEMP_FILE_SUFFIX);
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            List<Currency> currencies = parseExcel(tempFile.toFile());
            currencyService.saveAllCurrencies(currencies);
        } catch (IOException e) {
            log.error("Failed to load currencies {}", e.getLocalizedMessage());
        }
    }

    private List<Currency> parseExcel(File file) throws IOException {
        List<Currency> currencies = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            int skipRows = 3;
            int currentRow = 0;

            for (Row row : sheet) {
                if (currentRow++ <= skipRows) {
                    continue;
                }

                Cell codeCell = row.getCell(0);
                Cell nameCell = row.getCell(2);

                if (codeCell == null || nameCell == null) {
                    continue;
                }

                String name = codeCell.getStringCellValue().trim();
                String code = nameCell.getStringCellValue().trim();

                if (!code.isEmpty()) {
                    currencies.add(new Currency(code, name));
                }
            }
        }
        return currencies;
    }
}