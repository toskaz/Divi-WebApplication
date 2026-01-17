package com.example.divi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    @JoinColumn (name="groupId")
    @JsonIgnoreProperties("payments")
    private Group group;

    @ManyToOne
    @JoinColumn (name="userId")
    @JsonIgnoreProperties("payments")
    private User user;

    @ManyToOne
    @JoinColumn (name="currencyCode")
    @JsonIgnoreProperties("payments")
    private Currency currencyCode;

    @Column(length = 50)
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    private LocalDateTime date;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal defaultCurrencyAmount;

    private Boolean isCustomRate;

    private Boolean isExpense;
    
    @OneToMany(
            mappedBy = "payment",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties("payment")
    private List<Split> splits;

    @PrePersist
    protected void onCreate() {
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }

}
