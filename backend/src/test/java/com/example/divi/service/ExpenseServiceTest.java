package com.example.divi.service;

import com.example.divi.DTO.ExpenseContextDTO;
import com.example.divi.DTO.ExpenseRequestDTO;
import com.example.divi.model.*;
import com.example.divi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private SplitRepository splitRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private UserRepository userRepository;
    @Mock private CurrencyService currencyService;
    @Mock private GroupService groupService;

    @InjectMocks
    private ExpenseService expenseService;

    private Group group;
    private User user1;
    private User user2;
    private User user3;
    private Currency usd;
    private Currency eur;

    @BeforeEach
    void setUp() {
        usd = new Currency("USD", "US Dollar", "$", null, null, null);
        eur = new Currency("EUR", "Euro", "€", null, null, null);

        group = new Group();
        group.setGroupId(1L);
        group.setDefaultCurrency(usd);

        user1 = new User(); user1.setUserId(101L); user1.setFullName("Alice");
        user2 = new User(); user2.setUserId(102L); user2.setFullName("Bob");
        user3 = new User(); user3.setUserId(103L); user3.setFullName("Charlie");
    }

    //test1: verify even splitting
    @Test
    void testAddExpense_EvenSplit_CalculatesCorrectly() {
        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setGroupId(1L);
        request.setPayerId(101L);
        request.setAmount(new BigDecimal("30.00"));
        request.setCurrencyCode("USD");

        mockBasicDependencies(request);
        mockParticipants(101L, 102L, 103L);

        expenseService.addExpense(request);

        ArgumentCaptor<Split> splitCaptor = ArgumentCaptor.forClass(Split.class);
        verify(splitRepository, times(3)).save(splitCaptor.capture());

        List<Split> capturedSplits = splitCaptor.getAllValues();
        for (Split split : capturedSplits) {
            assertEquals(0, new BigDecimal("10.00").compareTo(split.getShareAmount()), "Każdy powinien mieć 10.00");
        }
    }

    //test2: verify uneven spliting (100/3)
    @Test
    void testAddExpense_UnevenSplit_HandlesPennyRounding() {
        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setGroupId(1L);
        request.setPayerId(101L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrencyCode("USD");

        mockBasicDependencies(request);
        mockParticipants(101L, 102L, 103L);

        expenseService.addExpense(request);

        ArgumentCaptor<Split> splitCaptor = ArgumentCaptor.forClass(Split.class);
        verify(splitRepository, times(3)).save(splitCaptor.capture());

        BigDecimal totalSplitSum = splitCaptor.getAllValues().stream()
                .map(Split::getShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(0, new BigDecimal("100.00").compareTo(totalSplitSum), "Suma splitów musi wynosić 100.00");
    }

    //test3: no zero or negative amount
    @Test
    void testAddExpense_ZeroOrNegativeAmount_ThrowsException() {
        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setGroupId(1L);
        request.setPayerId(101L);
        request.setCurrencyCode("USD");
        request.setAmount(new BigDecimal("-10.00")); 

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(101L)).thenReturn(Optional.of(user1));
        when(currencyService.getCurrencyByCode("USD")).thenReturn(usd);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            expenseService.addExpense(request);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("greater than zero"));
    }

    //test 4: if the group has different currency than expense
    @Test 
    void testAddExpense_DifferentCurrency_ConvertsCorrectly() {
        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setGroupId(1L);
        request.setPayerId(101L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrencyCode("EUR"); 
        request.setIsCustomRate(false); 

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(101L)).thenReturn(Optional.of(user1));
        when(currencyService.getCurrencyByCode("EUR")).thenReturn(eur);

        BigDecimal exchangeRate = new BigDecimal("1.20");
        when(currencyService.getCurrentExchangeRate("EUR", "USD")).thenReturn(exchangeRate);

        mockParticipants(101L); 
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        expenseService.addExpense(request);
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        BigDecimal expectedAmountInDefaultCurrency = new BigDecimal("120.00"); 

        assertEquals(0, expectedAmountInDefaultCurrency.compareTo(savedPayment.getDefaultCurrencyAmount()),
                "Kwota powinna zostać przeliczona na walutę domyślną grupy");
    }

    //test5: uses custom rate instead of fetching from API
    @Test
    void testAddExpense_CustomExchangeRate_UsesProvidedRate() {
        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setGroupId(1L);
        request.setPayerId(101L);
        request.setAmount(new BigDecimal("50.00"));
        request.setCurrencyCode("EUR");
        request.setIsCustomRate(true);
        request.setExchangeRate(new BigDecimal("2.00")); 

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(101L)).thenReturn(Optional.of(user1));
        when(currencyService.getCurrencyByCode("EUR")).thenReturn(eur);

        mockParticipants(101L);
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());

        expenseService.addExpense(request);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        BigDecimal expectedAmount = new BigDecimal("100.00");

        assertEquals(0, expectedAmount.compareTo(savedPayment.getDefaultCurrencyAmount()),
                "Powinien zostać użyty custom rate (50 * 2.0 = 100)");

        verify(currencyService, never()).getCurrentExchangeRate(anyString(), anyString());
    }

    private void mockBasicDependencies(ExpenseRequestDTO request) {
        when(groupRepository.findById(request.getGroupId())).thenReturn(Optional.of(group));
        when(userRepository.findById(request.getPayerId())).thenReturn(Optional.of(user1));
        when(currencyService.getCurrencyByCode(request.getCurrencyCode())).thenReturn(usd);
    
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());
    }

    private void mockParticipants(Long... userIds) {
        List<ExpenseContextDTO.ParticipantDTO> participants = new ArrayList<>();
        for (Long id : userIds) {
            participants.add(new ExpenseContextDTO.ParticipantDTO(id, "User " + id));

            if (id.equals(101L)) when(userRepository.findById(101L)).thenReturn(Optional.of(user1));
            if (id.equals(102L)) when(userRepository.findById(102L)).thenReturn(Optional.of(user2));
            if (id.equals(103L)) when(userRepository.findById(103L)).thenReturn(Optional.of(user3));
        }

        ExpenseContextDTO contextDTO = new ExpenseContextDTO(participants, "USD", "USD", BigDecimal.ONE, null, 101L);
        when(groupService.getExpenseContext(1L)).thenReturn(contextDTO);
    }
}

