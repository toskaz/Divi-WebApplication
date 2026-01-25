package com.example.divi.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    @JoinColumn (name="groupId")
    private Group group;

    @ManyToOne
    @JoinColumn (name="userId")
    private User user;

    @ManyToOne
    @JoinColumn (name="currencyCode")
    private Currency currency;

    @Column(length = 50)
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    private LocalDate date;

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
    @JsonIgnore
    private List<Split> splits = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

}
