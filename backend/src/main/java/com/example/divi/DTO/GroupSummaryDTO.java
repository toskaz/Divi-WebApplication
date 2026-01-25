package com.example.divi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GroupSummaryDTO {
    private Long groupId;
    private String groupName;
    private String currencyCode;
    private BigDecimal yourBalance;
    private Integer lastPaymentDaysAgo;
    private int memberCount;
}
