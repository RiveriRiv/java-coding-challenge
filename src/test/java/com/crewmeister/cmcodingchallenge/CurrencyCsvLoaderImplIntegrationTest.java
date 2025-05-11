package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.currency.config.CurrencyCsvProperties;
import com.crewmeister.cmcodingchallenge.currency.entity.Currency;
import com.crewmeister.cmcodingchallenge.currency.loader.impl.CurrencyCsvLoaderImpl;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CurrencyCsvLoaderImplIntegrationTest {

    @Autowired
    private CurrencyCsvLoaderImpl currencyCsvLoader;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private CurrencyCsvProperties properties;

    @Test
    void loadCurrencies_ShouldParseAndSaveCurrencies(@TempDir Path tempDir) throws Exception {
        File testExcel = tempDir.resolve("test.xlsx").toFile();
        generateTestExcelFile(testExcel);

        String fileUrl = testExcel.toURI().toURL().toString();
        when(properties.getUrl()).thenReturn(fileUrl);

        currencyCsvLoader.loadCurrencies();

        verify(currencyService, times(1)).saveAllCurrencies(argThat(currencies -> {
            if (currencies.size() != 2)
            {
                return false;
            }

            Currency c1 = currencies.get(0);
            Currency c2 = currencies.get(1);

            boolean istFirstRawValid = (c1.getCode().equals("USD") && c1.getName().equals("US Dollar")) ||
                    (c2.getCode().equals("USD") && c2.getName().equals("US Dollar"));
            boolean istSecondRawValid = (c1.getCode().equals("EUR") && c1.getName().equals("Euro")) ||
                    (c2.getCode().equals("EUR") && c2.getName().equals("Euro"));

            return istFirstRawValid && istSecondRawValid;
        }));
    }

    private void generateTestExcelFile(File file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            for (int i = 0; i < 4; i++) {
                sheet.createRow(i);
            }

            Row row1 = sheet.createRow(4);
            row1.createCell(0).setCellValue("US Dollar");
            row1.createCell(2).setCellValue("USD");

            Row row2 = sheet.createRow(5);
            row2.createCell(0).setCellValue("Euro");
            row2.createCell(2).setCellValue("EUR");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }
}

