package com.example.divi.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GroupDetailsDTO {
    private String groupName;
    private String currencyCode;
    private String currencySymbol;
    private BigDecimal totalExpenses;
    private int expenseCount;
    private int memberCount;
    private Long currentUserId;
}
