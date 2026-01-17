package com.example.divi.service;

import com.example.divi.DTO.ExpenseRequestDTO;
import com.example.divi.DTO.ExpenseResponseDTO;
import com.example.divi.model.*;
import com.example.divi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        Currency currency = currencyService.getCurrency(expenseRequest.getCurrencyCode());

        Payment payment = new Payment();
        payment.setGroup(group);
        payment.setUser(payer);
        payment.setCurrencyCode(currency);
        payment.setDescription(expenseRequest.getDescription());
        payment.setAmount(expenseRequest.getAmount());
        payment.setIsExpense(true);
        payment.setIsCustomRate(false);

        //ta sama waluta
        payment.setDefaultCurrencyAmount(expenseRequest.getAmount());
        Payment savedPayment = paymentRepository.save(payment);


        if (expenseRequest.getSplitDetails() != null) {
            expenseRequest.getSplitDetails().forEach((userId, shareAmount) -> {
                User debtor = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("user not found"));

                Split split = new Split();
                split.setPayment(savedPayment);
                split.setUser(debtor);
                split.setShareAmount(shareAmount);

                // TODO: ta sama waluta
                split.setShareDefaultCurrencyAmount(shareAmount);

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
