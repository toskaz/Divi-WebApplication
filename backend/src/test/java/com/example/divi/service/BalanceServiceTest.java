package com.example.divi.service;

import com.example.divi.DTO.BalanceDTO;
import com.example.divi.DTO.SettlementDTO;
import com.example.divi.model.*;
import com.example.divi.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private BalanceService balanceService;

    private Group group;
    private User userA;
    private User userB;
    private User userC;
    private Currency usd;

    @BeforeEach
    void setUp() {
        usd = new Currency("USD", "US Dollar", "$", null, null, null);

        userA = new User(); userA.setUserId(1L); userA.setFullName("Mati");
        userB = new User(); userB.setUserId(2L); userB.setFullName("Kasia");
        userC = new User(); userC.setUserId(3L); userC.setFullName("Tosia");

        group = new Group();
        group.setGroupId(100L);
        group.setDefaultCurrency(usd);
        group.setPayments(new ArrayList<>());
        
        Membership m1 = new Membership(); m1.setUser(userA); m1.setGroup(group);
        Membership m2 = new Membership(); m2.setUser(userB); m2.setGroup(group);
        Membership m3 = new Membership(); m3.setUser(userC); m3.setGroup(group);
        group.setMemberships(Arrays.asList(m1, m2, m3));
    }

    // test 1: simple debt 
    @Test
    void testCalculateBalances_SimpleDebt() {

        Payment payment = new Payment();
        payment.setUser(userA);
        payment.setDefaultCurrencyAmount(new BigDecimal("50.00"));
        
        Split splitA = new Split(); 
        splitA.setUser(userA); 
        splitA.setShareDefaultCurrencyAmount(new BigDecimal("25.00"));

        Split splitB = new Split(); 
        splitB.setUser(userB); 
        splitB.setShareDefaultCurrencyAmount(new BigDecimal("25.00"));

        payment.setSplits(Arrays.asList(splitA, splitB));
        group.getPayments().add(payment);

        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        List<BalanceDTO> results = balanceService.calculateGroupBalances(100L);

        BalanceDTO balanceMati = results.stream()
                .filter(b -> b.getUserId().equals(userB.getUserId()))
                .findFirst().orElseThrow();

        BalanceDTO balanceKasia = results.stream()
                .filter(b -> b.getUserId().equals(userA.getUserId()))
                .findFirst().orElseThrow();

        assertEquals(0, new BigDecimal("-25.00").compareTo(balanceMati.getBalance()), "Bob should owe 25.00");
        assertEquals(0, new BigDecimal("25.00").compareTo(balanceKasia.getBalance()), "Alice should be owed 25.00");
    }

    // test2: dlug miedzy A->B i B->C to A->C
    @Test
    void testGetSettlementPlan_GeneratesCorrectTransfers() {
        Payment p1 = new Payment();
        p1.setUser(userB); 
        p1.setDefaultCurrencyAmount(new BigDecimal("10.00"));
        Split s1 = new Split(); s1.setUser(userA); s1.setShareDefaultCurrencyAmount(new BigDecimal("10.00"));
        p1.setSplits(Arrays.asList(s1));

        Payment p2 = new Payment();
        p2.setUser(userC);
        p2.setDefaultCurrencyAmount(new BigDecimal("10.00"));
        Split s2 = new Split(); s2.setUser(userB); s2.setShareDefaultCurrencyAmount(new BigDecimal("10.00"));
        p2.setSplits(Arrays.asList(s2));

        group.getPayments().addAll(Arrays.asList(p1, p2));

        when(groupRepository.findById(100L)).thenReturn(Optional.of(group));
        List<SettlementDTO> settlements = balanceService.getSettlementPlan(100L);

        assertEquals(1, settlements.size(), "Should simplify to exactly 1 transfer");
        
        SettlementDTO transfer = settlements.get(0);
        
        assertEquals(userA.getUserId(), transfer.getFromUserId(), "Alice should be the payer");
        
        assertEquals(userC.getUserId(), transfer.getToUserId(), "Charlie should be the recipient");
        
        assertEquals(0, new BigDecimal("10.00").compareTo(transfer.getAmount()), "Amount should be 10.00");
    }
}