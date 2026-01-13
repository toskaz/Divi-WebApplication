package com.example.divi.repository;

import com.example.divi.model.Group;
import com.example.divi.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentReposiotry extends JpaRepository<Payment,Long> {
    List<Payment> findByGroupOrderByDateDesc(Group group);
    List<Payment> findByGroup_GroupIdOrderByDateDesc(Long groupId);
    List<Payment> findByUser_UserId(Long userId);

}
