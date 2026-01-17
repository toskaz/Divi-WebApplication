package com.example.divi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GroupSummaryDTO {
    private Long groupId;
    private String groupName;
    private String currencyCode;
    private BigDecimal yourBalance;
    private LocalDateTime lastPaymentDate;
    private int memberCount;
}
