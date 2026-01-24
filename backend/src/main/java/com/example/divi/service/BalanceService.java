package com.example.divi.service;

import com.example.divi.DTO.BalanceDTO;
import com.example.divi.DTO.SettlementDTO;
import com.example.divi.model.Group;
import com.example.divi.model.Membership;
import com.example.divi.model.Payment;
import com.example.divi.model.Split;
import com.example.divi.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BalanceService {
    @Autowired
    private GroupRepository groupRepository;

    public List<BalanceDTO> calculateGroupBalances(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group with ID '" + groupId + "' not found"));

        Map<Long, BigDecimal> balances = new HashMap<>();
        Map<Long, String> userNames = new HashMap<>();

        for (Membership member : group.getMemberships()) {
            balances.put(member.getUser().getUserId(), BigDecimal.ZERO);
            userNames.put(member.getUser().getUserId(), member.getUser().getFullName());
        }

        for (Payment payment : group.getPayments()) {
            BigDecimal totalAmount = payment.getDefaultCurrencyAmount();
            Long payerId = payment.getUser().getUserId();

            balances.put(payerId, balances.getOrDefault(payerId, BigDecimal.ZERO).add(totalAmount));


            for (Split split : payment.getSplits()) {
                Long debtorId = split.getUser().getUserId();
                BigDecimal splitAmount = split.getShareAmount();

                balances.put(debtorId, balances.getOrDefault(debtorId, BigDecimal.ZERO).subtract(splitAmount));
            }


        }
        List<BalanceDTO> result = new ArrayList<>();
        balances.forEach((userId, amount) -> {
            result.add(new BalanceDTO(userId, userNames.get(userId), amount, group.getDefaultCurrency().getCurrencyCode()));
        });

        return result;
    }

    public BigDecimal getUserBalanceInGroup(Long userId, Long groupId) {
        List<BalanceDTO> allBalances = calculateGroupBalances(groupId);

        return allBalances.stream()
                .filter(b -> b.getUserId().equals(userId))
                .findFirst()
                .map(BalanceDTO::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    public List<SettlementDTO> getSettlementPlan(Long groupId) {
        List<BalanceDTO> balances = calculateGroupBalances(groupId);

        if (balances.isEmpty()) {
            return new ArrayList<>();
        }

        String currency = balances.get(0).getCurrencyCode();

        List<BalanceDTO> debtors = new ArrayList<>();
        List<BalanceDTO> creditors = new ArrayList<>();

        for (BalanceDTO b : balances) {
            if (b.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(b);
            } else if (b.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(b);
            }
        }

        List<SettlementDTO> settlements = new ArrayList<>();
        int debtIndex = 0;
        int credIndex = 0;

        while (debtIndex < debtors.size() && credIndex < creditors.size()) {
            BalanceDTO debtor = debtors.get(debtIndex);
            BalanceDTO creditor = creditors.get(credIndex);

            BigDecimal debtAmount = debtor.getBalance().abs();
            BigDecimal creditAmount = creditor.getBalance();

            BigDecimal transferAmount = debtAmount.min(creditAmount);

            settlements.add(new SettlementDTO(
                    debtor.getUserId(), debtor.getFullName(),
                    creditor.getUserId(), creditor.getFullName(),
                    transferAmount, currency
            ));


            debtor.setBalance(debtor.getBalance().add(transferAmount));
            creditor.setBalance(creditor.getBalance().subtract(transferAmount));

            if (debtor.getBalance().abs().compareTo(new BigDecimal("0.01")) < 0) {
                debtIndex++;
            }

            if (creditor.getBalance().abs().compareTo(new BigDecimal("0.01")) < 0) {
                credIndex++;
            }
        }

        return settlements;
    }

}
