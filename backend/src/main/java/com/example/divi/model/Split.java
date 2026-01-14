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
public class Split {

    @EmbeddedId
    private UserPaymentId id = new UserPaymentId();

    @ManyToOne
    @MapsId("paymentId")
    @JoinColumn(name = "paymentId")
    @JsonIgnoreProperties("splits")
    private Payment payment;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties("splits")
    private User user;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal shareAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal shareDefaultCurrencyAmount;

}
