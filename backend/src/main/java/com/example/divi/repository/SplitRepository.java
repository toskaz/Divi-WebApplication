package com.example.divi.repository;

import com.example.divi.model.Payment;
import com.example.divi.model.Split;
import com.example.divi.model.UserPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitRepository extends JpaRepository<Split, UserPaymentId> {
    List<Split> findByPayment(Payment payment);
    List<Split> findByPayment_PaymentId(Long paymentId);
    List<Split> findByUser_UserId(Long userId);
}
