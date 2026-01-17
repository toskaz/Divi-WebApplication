package com.example.divi.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ExpenseRequestDTO {
    private String description;
    private BigDecimal amount;
    private String currencyCode;
    private BigDecimal exchangeRate;
    private Long payerId;
    private Long groupId;
    private LocalDateTime date;

    private Map<Long, BigDecimal> splitDetails;
}
