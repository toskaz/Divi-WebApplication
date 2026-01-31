package com.example.divi.controller;

import com.example.divi.DTO.RegisterRequestDTO;
import com.example.divi.model.User;
import com.example.divi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testRegister_DuplicateEmail_Returns409() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("duplicate@test.com");
        existingUser.setFullName("Original User");
        existingUser.setPassword("pass");
        userRepository.save(existingUser);

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setName("Imposter");
        request.setEmail("duplicate@test.com"); 
        request.setPassword("newpass");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError()); 
    }
}