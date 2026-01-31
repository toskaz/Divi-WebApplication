package com.example.divi.service;

import com.example.divi.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
    
        ReflectionTestUtils.setField(currencyService, "restTemplate", restTemplate);
    }

    //test1: external api failure
    @Test
    void testGetCurrentExchangeRate_ExternalApiFailure() {
        String from = "USD";
        String to = "EUR";

        HttpServerErrorException apiException = 
            new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "External Server Down");
            
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenThrow(apiException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currencyService.getCurrentExchangeRate(from, to);
        });

        String actualMessage = exception.getMessage();
        
        assertTrue(actualMessage.contains("API error"), "Should start with 'API error'");
        assertTrue(actualMessage.contains("External Server Down"), "Should contain the API status text");
    }
}