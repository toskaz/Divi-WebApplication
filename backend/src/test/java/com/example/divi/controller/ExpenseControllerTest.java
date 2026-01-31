package com.example.divi.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Collections;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.example.divi.DTO.ExpenseRequestDTO;
import com.example.divi.model.*;
import com.example.divi.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@Transactional      
public class ExpenseControllerTest {

    @Autowired 
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired private UserRepository userRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private CurrencyRepository currencyRepository;
    @Autowired private MembershipRepository membershipRepository;

    private User alice;
    private Group group;

    @BeforeEach
    void setUp() {
        Currency usd = currencyRepository.findByCurrencyCode("USD")
                .orElseGet(() -> currencyRepository.save(new Currency("USD", "US Dollar", "$", null, null, null)));

        alice = new User();
        alice.setEmail("alice_exp_ctrl@test.com");
        alice.setFullName("Alice Exp");
        alice.setPassword("password");
        alice = userRepository.save(alice);

        group = new Group();
        group.setGroupName("Expense Test Group");
        group.setDefaultCurrency(usd);
        group.setCurrentCurrency(usd);
        group = groupRepository.save(group);

        Membership m = new Membership();
        m.setUser(alice);
        m.setGroup(group);
        membershipRepository.save(m);

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(alice, null, alice.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    @Test
    void testAddExpense_ValidRequest_Returns200() throws Exception {

        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setGroupId(group.getGroupId());
        request.setPayerId(alice.getUserId());
        request.setAmount(new BigDecimal("50.00"));
        request.setCurrencyCode("USD");
        request.setDescription("Lunch");
        request.setDate(null); 
        request.setIsCustomRate(false);   
        request.setSplitDetails(Collections.singletonMap(alice.getUserId(), new BigDecimal("50.00")));

        mockMvc.perform(post("/api/expenses")
                .with(user(alice)) 
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) 
                .andExpect(status().isOk());
    }

    @Test
    void testAddExpense_InvalidData_Returns400() throws Exception {
        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setGroupId(group.getGroupId());
        request.setCurrencyCode("USD");

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}