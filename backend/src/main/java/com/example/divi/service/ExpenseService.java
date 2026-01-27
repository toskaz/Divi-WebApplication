package com.example.divi.service;

import com.example.divi.DTO.ExpenseContextDTO;
import com.example.divi.DTO.ExpenseRequestDTO;
import com.example.divi.DTO.ExpenseResponseDTO;
import com.example.divi.DTO.ExpenseContextDTO.ParticipantDTO;
import com.example.divi.model.*;
import com.example.divi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private GroupService groupService;

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

        if (expenseRequest.getAmount() == null || expenseRequest.getAmount().compareTo(BigDecimal.valueOf(0.01)) < 0) {
            throw new RuntimeException("Expense amount must be greater than zero");
        }

        BigDecimal expenseAmount = expenseRequest.getAmount().setScale(2, RoundingMode.DOWN);
        if (expenseRequest.getSplitDetails() != null && expenseRequest.getSplitDetails().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(expenseAmount) != 0) {
            throw new RuntimeException("Sum of split amounts must equal the total expense amount");
        }
        
        Map<Long, BigDecimal> splitDetails = (expenseRequest.getSplitDetails() == null ? new HashMap<>() : expenseRequest.getSplitDetails());
        ExpenseContextDTO expenseContextDTO = groupService.getExpenseContext(expenseRequest.getGroupId());
        List<ParticipantDTO> participants = expenseContextDTO.getParticipants();
        List<Long> participantsIds = participants.stream().map(ParticipantDTO::getId).toList();

        if (splitDetails.isEmpty()) { // test
            int membersCount = participants.size();
            BigDecimal equalAmount = expenseAmount.divide(BigDecimal.valueOf(membersCount), RoundingMode.DOWN);
            participants.forEach(p -> splitDetails.put(p.getId(), equalAmount));
            BigDecimal sumOfEqualShares = equalAmount.multiply(BigDecimal.valueOf(membersCount));
            BigDecimal remainder = expenseAmount.subtract(sumOfEqualShares);
            if (remainder.compareTo(BigDecimal.ZERO) != 0) {
                int biggerSharesCount = remainder.divide(BigDecimal.valueOf(0.01)).intValue();
                List<Long> shuffledMemberIds = new ArrayList<Long>(participantsIds);
                Collections.shuffle(shuffledMemberIds);
                shuffledMemberIds = shuffledMemberIds.subList(0, biggerSharesCount);
                shuffledMemberIds.forEach(shuffledId -> splitDetails.put(shuffledId, splitDetails.get(shuffledId).add(BigDecimal.valueOf(0.01))));
                splitDetails.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);
            }
        }

        Payment payment = new Payment();
        payment.setGroup(group);
        payment.setUser(payer);
        payment.setCurrency(transactionCurrency);
        payment.setDescription(expenseRequest.getDescription());
        payment.setAmount(expenseAmount);
        payment.setIsExpense(true);
        
        Boolean isCustomRate = expenseRequest.getIsCustomRate() != null ? expenseRequest.getIsCustomRate() : false;
        payment.setIsCustomRate(isCustomRate);

        if (expenseRequest.getDate() != null) {
            payment.setDate(expenseRequest.getDate());
        }

        BigDecimal amountInDefaultCurrency = expenseAmount;
        BigDecimal rate = BigDecimal.ONE;
        boolean differentCurrencies = !transactionCurrency.getCurrencyCode().equals(groupDefaultCurrency.getCurrencyCode());
        if (differentCurrencies) {
            if (isCustomRate) {
                rate = expenseRequest.getExchangeRate();
            } else {
                rate = currencyService.getCurrentExchangeRate(transactionCurrency.getCurrencyCode(), groupDefaultCurrency.getCurrencyCode());
            }
            amountInDefaultCurrency = amountInDefaultCurrency.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }
        payment.setDefaultCurrencyAmount(amountInDefaultCurrency);

        Payment savedPayment = paymentRepository.save(payment);

        final BigDecimal finalRate = rate;
        Map<Long, BigDecimal> splitDefaultCurrencyDetails = new HashMap<>(splitDetails);
        splitDefaultCurrencyDetails.forEach((id, shareAmount) -> {
            if (differentCurrencies) {
                splitDefaultCurrencyDetails.put(id, splitDefaultCurrencyDetails.get(id).multiply(finalRate).setScale(2, RoundingMode.HALF_UP));
            }
        });

        BigDecimal splitDefaultCurrencyDetailsSum = splitDefaultCurrencyDetails.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainder = amountInDefaultCurrency.subtract(splitDefaultCurrencyDetailsSum);
        if (remainder.compareTo(BigDecimal.ZERO) != 0) {
            int alteredSharesCount = remainder.movePointRight(2).abs().intValue();
            BigDecimal adjustment = remainder.signum() > 0 ? new BigDecimal("0.01") : new BigDecimal("-0.01");
            List<Long> shuffledMemberIds = new ArrayList<Long>(participantsIds);
            Collections.shuffle(shuffledMemberIds);
            shuffledMemberIds = shuffledMemberIds.subList(0, alteredSharesCount);
            shuffledMemberIds.forEach(shuffledId -> splitDefaultCurrencyDetails.put(shuffledId, splitDefaultCurrencyDetails.get(shuffledId).add(adjustment)));
        }

        splitDetails.forEach((userId, shareAmount) -> {
            User debtor = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

            Split split = new Split();
            split.setPayment(savedPayment);
            split.setUser(debtor);
            split.setShareAmount(shareAmount);
            split.setShareDefaultCurrencyAmount(splitDefaultCurrencyDetails.get(userId));

            splitRepository.save(split);
        });

        return savedPayment;
    }

    @Transactional
    public List<ExpenseResponseDTO> getExpensesByGroupId(Long groupId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        Group group = groupRepository.findById(groupId).get();
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
