package com.example.divi.controller;

import com.example.divi.DTO.ExpenseRequestDTO;
import com.example.divi.model.Payment;
import com.example.divi.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Payment> addExpense(@RequestBody ExpenseRequestDTO requestExpense) {
        Payment payment = expenseService.addExpense(requestExpense);
        return ResponseEntity.ok(payment);
    }

}
