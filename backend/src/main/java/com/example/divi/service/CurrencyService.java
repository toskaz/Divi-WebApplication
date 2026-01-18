package com.example.divi.service;

import com.example.divi.model.Currency;
import com.example.divi.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_KEY = "e25866d2451f6df4721a4c3b";
    private final String API_URL_BASE = "https://v6.exchangerate-api.com/v6/";

    public Currency getCurrency(String code) {
        return currencyRepository.findByCurrencyCode(code).orElse(null);
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }

        try {
            String url = API_URL_BASE + API_KEY + "/pair/" + fromCurrency + "/" + toCurrency;

            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            String result = root.path("result").asText();
            if (!"success".equalsIgnoreCase(result)) {
                throw new RuntimeException("API error: " + root.path("error-type").asText());
            }
            double rate = root.path("conversion_rate").asDouble();

            return BigDecimal.valueOf(rate);

        } catch (Exception e) {
            throw new RuntimeException("fail to connect to API", e);
        }
    }
}
