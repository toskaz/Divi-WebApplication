package com.example.divi.security;

import com.example.divi.service.JwtService;
import com.example.divi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testToken_TamperedSignature_IsRejected() {
       
        User mockUser = new User();
        mockUser.setEmail("hacker_test@test.com");
        String validToken = jwtService.generateToken(mockUser);

        assertNotNull(validToken);
        assertTrue(validToken.length() > 0);

        String[] parts = validToken.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + "FAKED" + "." + parts[2];

        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(tamperedToken);
        });
    }
}