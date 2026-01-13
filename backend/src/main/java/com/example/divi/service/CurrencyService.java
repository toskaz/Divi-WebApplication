package com.example.divi.service;

import com.example.divi.model.Currency;
import com.example.divi.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepository;

    public Currency getCurrency(String code) {
        return currencyRepository.findByCurrencyCode(code).orElse(null);
    }
}
