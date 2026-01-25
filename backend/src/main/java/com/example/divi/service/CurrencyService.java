package com.example.divi.service;

import com.example.divi.model.Currency;
import com.example.divi.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
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

    public Currency getCurrencyByCode(String code) {
        return currencyRepository.findByCurrencyCode(code).orElse(null);
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public BigDecimal getCurrentExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }

        String url = API_URL_BASE + API_KEY + "/pair/" + fromCurrency + "/" + toCurrency;
        ObjectMapper mapper = new ObjectMapper();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);

            double rate = root.path("conversion_rate").asDouble();
            return BigDecimal.valueOf(rate);
        } catch (HttpStatusCodeException e) {
            String errorBody = e.getResponseBodyAsString();
            String errorType = "";

            try {
                JsonNode errorNode = mapper.readTree(errorBody);
                errorType = errorNode.path("error-type").asString();
            } catch (Exception parseException) {
                errorType = e.getStatusText();
            }

            throw new RuntimeException("API error: " + e.getStatusText() + " for " + url + ": " + errorType);
        } catch (Exception e) {
            throw new RuntimeException("Internal error: " + e.getMessage());
        }
    }
}
