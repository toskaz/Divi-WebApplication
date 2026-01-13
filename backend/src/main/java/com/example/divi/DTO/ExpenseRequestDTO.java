package com.example.divi.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExpenseRequestDTO {
    private String description;
    private BigDecimal amount;
    private String currencyCode;
    private Long payerId;
    private Long groupId;

    private Map<Long, BigDecimal> splitDetails;
}
