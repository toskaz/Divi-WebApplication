package com.example.divi.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "splits")
@NoArgsConstructor
@AllArgsConstructor
public class Split {

    @EmbeddedId
    private UserPaymentId id = new UserPaymentId();

    @ManyToOne
    @MapsId("paymentId")
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal shareAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal shareDefaultCurrencyAmount;

}
