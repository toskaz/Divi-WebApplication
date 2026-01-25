package com.example.divi.service;

import com.example.divi.DTO.ExpenseRequestDTO;
import com.example.divi.DTO.ExpenseResponseDTO;
import com.example.divi.model.*;
import com.example.divi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                .orElseThrow(() -> new RuntimeException("Group with ID " + expenseRequest.getGroupId() + " not found"));

        User payer = userRepository.findById(expenseRequest.getPayerId())
                .orElseThrow(() -> new RuntimeException("User with ID " + expenseRequest.getPayerId() + " not found"));

        Currency groupDefaultCurrency = group.getDefaultCurrency();
        Currency transactionCurrency = currencyService.getCurrencyByCode(expenseRequest.getCurrencyCode());
        if (transactionCurrency == null) {
            throw new RuntimeException("Currency with code '" + expenseRequest.getCurrencyCode() + "' not found");
        }

        if (expenseRequest.getSplitDetails() == null || expenseRequest.getSplitDetails().isEmpty()) {
            throw new RuntimeException("At least one split detail must be provided");
        }

        if (expenseRequest.getAmount() == null || expenseRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Expense amount must be greater than zero");
        }

        if (expenseRequest.getSplitDetails().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(expenseRequest.getAmount()) != 0) {
            throw new RuntimeException("Sum of split amounts must equal the total expense amount");
        }

        BigDecimal rate = BigDecimal.ONE;
        Boolean isCustomRate = expenseRequest.getIsCustomRate() != null ? expenseRequest.getIsCustomRate() : false;

        if (!transactionCurrency.getCurrencyCode().equals(groupDefaultCurrency.getCurrencyCode())) {
            if (isCustomRate) {
                rate = expenseRequest.getExchangeRate();
            } else {
                rate = currencyService.getCurrentExchangeRate(transactionCurrency.getCurrencyCode(), groupDefaultCurrency.getCurrencyCode());
            }
        }

        Payment payment = new Payment();
        payment.setGroup(group);
        payment.setUser(payer);
        payment.setCurrency(transactionCurrency);
        payment.setDescription(expenseRequest.getDescription());
        payment.setAmount(expenseRequest.getAmount());
        payment.setIsExpense(true);
        payment.setIsCustomRate(isCustomRate);

        if (expenseRequest.getDate() != null) {
            payment.setDate(expenseRequest.getDate());
        }

        BigDecimal amountInDefaultCurrency = expenseRequest.getAmount().multiply(rate);
        payment.setDefaultCurrencyAmount(amountInDefaultCurrency);

        Payment savedPayment = paymentRepository.save(payment);


        final BigDecimal finalRate = rate;

        expenseRequest.getSplitDetails().forEach((userId, shareAmount) -> {
            User debtor = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

            Split split = new Split();
            split.setPayment(savedPayment);
            split.setUser(debtor);

            split.setShareAmount(shareAmount);

            split.setShareDefaultCurrencyAmount(shareAmount.multiply(finalRate));

            splitRepository.save(split);
        });

        return savedPayment;
    }

    @Transactional
    public List<ExpenseResponseDTO> getExpensesByGroupId(Long groupId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email '" + email + "' not found"));
        List<Payment> payments = paymentRepository.findByGroup_GroupIdOrderByDateDesc(groupId);
        return payments.stream().map(payment -> {
            ExpenseResponseDTO expenseResponse = new ExpenseResponseDTO();
            expenseResponse.setPaymentId(payment.getPaymentId());
            expenseResponse.setDescription(payment.getDescription());
            expenseResponse.setAmount(payment.getAmount());
            expenseResponse.setCurrencyCode(payment.getCurrency().getCurrencyCode());
            expenseResponse.setCurrencySymbol(payment.getCurrency().getCurrencySymbol());
            expenseResponse.setPayerName(payment.getUser().getFullName());
            expenseResponse.setDate(payment.getDate());
            int count = (payment.getSplits() != null) ? payment.getSplits().size() : 0;
            expenseResponse.setInvolvedPeopleCount(count);

            BigDecimal myShare = BigDecimal.ZERO;

            if (payment.getSplits() != null) {
                myShare = payment.getSplits().stream()
                        .filter(split -> split.getUser().getUserId().equals(currentUser.getUserId()))
                        .findFirst()
                        .map(split -> split.getShareAmount())
                        .orElse(BigDecimal.ZERO);
            }
            expenseResponse.setYourShare(myShare);

            return expenseResponse;
        }).toList();
    }


    @Transactional
    public void settleDebt(Long groupId, Long fromUserId, Long toUserId, BigDecimal amount) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group with ID " + groupId + " not found"));

        User payer = userRepository.findById(fromUserId).orElseThrow(() -> new RuntimeException("Payer not found"));
        User recipient = userRepository.findById(toUserId).orElseThrow(() -> new RuntimeException("Recipient not found"));

        Payment payment = new Payment();
        payment.setGroup(group);
        payment.setUser(payer);
        payment.setAmount(amount);
        payment.setDefaultCurrencyAmount(amount);
        payment.setCurrency(group.getDefaultCurrency());
        payment.setDescription("Settlement: " + payer.getFullName() + " -> " + recipient.getFullName());
        payment.setDate(LocalDate.now());
        payment.setIsExpense(false);
        payment.setIsCustomRate(false);

        Payment savedPayment = paymentRepository.save(payment);

        Split split = new Split();
        split.setPayment(savedPayment);
        split.setUser(recipient);
        split.setShareAmount(amount);
        split.setShareDefaultCurrencyAmount(amount);

        splitRepository.save(split);
    }

}
