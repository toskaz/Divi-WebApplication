package com.example.divi.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class ExpenseRequestDTO {
    private String description;
    private BigDecimal amount;
    private String currencyCode;
    private BigDecimal exchangeRate;
    private Boolean isCustomRate;
    private Long payerId;
    private Long groupId;
    private LocalDate date;

    private Map<Long, BigDecimal> splitDetails;
}
