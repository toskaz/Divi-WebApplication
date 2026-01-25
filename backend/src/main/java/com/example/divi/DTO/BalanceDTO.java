package com.example.divi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceDTO {
    private Long userId;
    private String fullName;
    private BigDecimal balance;
    private String currencySymbol;
}
