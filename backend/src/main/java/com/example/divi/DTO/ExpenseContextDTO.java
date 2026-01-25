package com.example.divi.DTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseContextDTO {
    private List<ParticipantDTO> participants;
    private String defaultCurrencyCode;
    private String currentCurrencyCode;
    private BigDecimal currentExchangeRate;
    private List<String> availableCurrencyCodes;
    private Long currentUserId;
    
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ParticipantDTO {
        private Long id;
        private String name;
    }
}
