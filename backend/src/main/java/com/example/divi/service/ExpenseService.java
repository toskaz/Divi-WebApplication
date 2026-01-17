package com.example.divi.service;

import com.example.divi.DTO.ExpenseRequestDTO;
import com.example.divi.DTO.ExpenseResponseDTO;
import com.example.divi.model.*;
import com.example.divi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SplitRepository splitRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrencyService currencyService;

    @Transactional
    public Payment addExpense(ExpenseRequestDTO expenseRequest) {
        Group group = groupRepository.findById(expenseRequest.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User payer = userRepository.findById(expenseRequest.getPayerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Currency transactionCurrency = currencyService.getCurrency(expenseRequest.getCurrencyCode());
        Currency groupCurrency = group.getDefaultCurrency();


        BigDecimal rate = BigDecimal.ONE;
        boolean isCustomRate = false;


        if (!transactionCurrency.getCurrencyCode().equals(groupCurrency.getCurrencyCode())) {
            if (expenseRequest.getExchangeRate() != null && expenseRequest.getExchangeRate().compareTo(BigDecimal.ZERO) > 0) {
                rate = expenseRequest.getExchangeRate();
                isCustomRate = true;
            } else {
                // TODO:Tutaj w przyszłości można dodać pobieranie kursu z API (np. NBP)
                throw new RuntimeException("Exchange rate is required for different currencies ("
                        + transactionCurrency.getCurrencyCode() + " -> " + groupCurrency.getCurrencyCode() + ")");
            }
        }


        Payment payment = new Payment();
        payment.setGroup(group);
        payment.setUser(payer);
        payment.setCurrencyCode(transactionCurrency);
        payment.setDescription(expenseRequest.getDescription());
        payment.setAmount(expenseRequest.getAmount());
        payment.setIsExpense(true);
        payment.setIsCustomRate(isCustomRate);

        if (expenseRequest.getDate() != null) {
            payment.setDate(expenseRequest.getDate());
        } else {
            payment.setDate(LocalDateTime.now());
        }

        BigDecimal amountInDefaultCurrency = expenseRequest.getAmount().multiply(rate);
        payment.setDefaultCurrencyAmount(amountInDefaultCurrency);

        Payment savedPayment = paymentRepository.save(payment);


        final BigDecimal finalRate = rate;

        if (expenseRequest.getSplitDetails() != null) {
            expenseRequest.getSplitDetails().forEach((userId, shareAmount) -> {
                User debtor = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Split split = new Split();
                split.setPayment(savedPayment);
                split.setUser(debtor);

                split.setShareAmount(shareAmount);

                split.setShareDefaultCurrencyAmount(shareAmount.multiply(finalRate));

                splitRepository.save(split);
            });
        }

        return savedPayment;
    }

    public List<ExpenseResponseDTO> getExpensesByGroupId(Long groupId) {
        List<Payment> payments = paymentRepository.findByGroup_GroupIdOrderByDateDesc(groupId);
        return payments.stream().map(payment -> {
            ExpenseResponseDTO expenseResponse = new ExpenseResponseDTO();
            expenseResponse.setPaymentId(payment.getPaymentId());
            expenseResponse.setDescription(payment.getDescription());
            expenseResponse.setAmount(payment.getAmount());
            expenseResponse.setCurrencyCode(payment.getCurrencyCode().getCurrencyCode());
            expenseResponse.setPayerName(payment.getUser().getFullName());
            expenseResponse.setDate(payment.getDate());
            return expenseResponse;
        }).toList();
    }

}
