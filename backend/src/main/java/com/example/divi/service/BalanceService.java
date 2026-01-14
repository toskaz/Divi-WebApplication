package com.example.divi.service;

import com.example.divi.DTO.BalanceDTO;
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
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Map<Long, BigDecimal> balances = new HashMap<>();
        Map<Long, String> userNames = new HashMap<>();

        for (Membership member : group.getMemberships()) {
            balances.put(member.getUser().getUserId(), BigDecimal.ZERO);
            userNames.put(member.getUser().getUserId(), member.getUser().getFullName());
        }

        for (Payment payment : group.getPayments()) {
            BigDecimal totalAmount = payment.getAmount();
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
}
