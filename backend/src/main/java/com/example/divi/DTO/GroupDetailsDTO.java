package com.example.divi.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GroupDetailsDTO {
    private Long groupId;
    private String groupName;
    private String currencyCode;
    private BigDecimal totalExpenses;
    private int expenseCount;
    private int memberCount;
}
