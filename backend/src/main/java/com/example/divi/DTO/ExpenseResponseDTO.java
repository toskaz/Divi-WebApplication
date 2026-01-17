package com.example.divi.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseResponseDTO {
    private Long paymentId;
    private String description;
    private BigDecimal amount;
    private String currencyCode;
    private String payerName;
    private LocalDateTime date;

    private BigDecimal yourShare;
    private int involvedPeopleCount;
}
