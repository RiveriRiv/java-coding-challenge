package com.crewmeister.cmcodingchallenge;

import com.crewmeister.cmcodingchallenge.currency.entity.Currency;
import com.crewmeister.cmcodingchallenge.currency.rate.entity.CurrencyConversionRate;
import com.crewmeister.cmcodingchallenge.currency.rate.repository.CurrencyConversionRateRepository;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CurrencyControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private CurrencyRepository currencyRepository;

	@Autowired
	private CurrencyConversionRateRepository rateRepository;

	private String baseUrl;

	@BeforeEach
	void setUp() {
		baseUrl = "http://localhost:" + port + "/api";

		rateRepository.deleteAll();
		currencyRepository.deleteAll();

		Currency usd = new Currency("USD");
		Currency eur = new Currency("EUR");
		currencyRepository.saveAll(List.of(usd, eur));

		rateRepository.save(
				new CurrencyConversionRate(1.2, "EUR", "USD", LocalDate.of(2021, 8, 10))
		);
	}

	@Test
	void testGetAllCurrencies() {
		ResponseEntity<Currency[]> response = restTemplate.getForEntity(baseUrl + "/currencies/all", Currency[].class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).hasSize(2);
		assertThat(response.getBody()[0].getCode()).isIn("EUR", "USD");
	}

	@Test
	void testGetCurrencyRateForParticularDate() {
		String url = baseUrl + "/currencies/USD/rates/2021-08-10";
		ResponseEntity<CurrencyConversionRate> response = restTemplate.getForEntity(url, CurrencyConversionRate.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		CurrencyConversionRate rate = response.getBody();
		assertThat(rate).isNotNull();
		assertThat(rate.getTargetCurrency()).isEqualTo("USD");
		assertThat(rate.getBaseCurrency()).isEqualTo("EUR");
		assertThat(rate.getDate()).isEqualTo(LocalDate.of(2021, 8, 10));
		assertThat(rate.getConversionRate()).isEqualTo(1.2);
	}

	@Test
	void testConvertToEur() {
		String url = baseUrl + "/currencies/USD/convert?amount=120&date=2021-08-10";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"convertedAmount\":100.0");
	}

	@Test
	void testGetPageableCurrencyRatesForAllDates() {
		String url = baseUrl + "/currencies/USD/rates?page=0&size=10";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"content\"");
		assertThat(response.getBody()).contains("\"targetCurrency\":\"USD\"");
	}
}

