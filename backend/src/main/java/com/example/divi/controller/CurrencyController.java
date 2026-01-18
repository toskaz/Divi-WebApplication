package com.example.divi.controller;

import com.example.divi.model.Currency;
import com.example.divi.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/currencies")
@CrossOrigin(origins = "http://localhost:5173")
public class CurrencyController {
    @Autowired
    private CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @GetMapping("/rate")
    public ResponseEntity<Map<String, BigDecimal>> getRate(@RequestParam String from, @RequestParam String to) {
        BigDecimal rate = currencyService.getExchangeRate(from, to);
        return ResponseEntity.ok(Map.of("rate", rate));
    }



}
