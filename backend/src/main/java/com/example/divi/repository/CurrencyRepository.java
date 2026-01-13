package com.example.divi.repository;

import com.example.divi.model.Currency;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository {
    Optional<Currency> findByCurrencyCode(String currencyCode);
}
