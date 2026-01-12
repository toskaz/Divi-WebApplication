package com.example.divi.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserPaymentId.class)
public class Split {

    @Id
    @ManyToOne
    @JoinColumn(name = "paymentId")
    @JsonIgnoreProperties("splits")
    private Payment payment;

    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties("splits")
    private User user;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal shareAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal shareDefaultCurrencyAmount;

}
