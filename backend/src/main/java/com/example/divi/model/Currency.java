package com.example.divi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {

    @Id
    private String currencyCode;

    @Column(length = 50, nullable = false)
    private String currencyName;

    @Column(length = 5, nullable = false)
    private String symbol;

    @OneToMany(
            mappedBy = "defaultCurrency",
            // cascade = CascadeType.REMOVE,
            // orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties("defaultCurrency")
    private List<Group> groupsDefault;

    @OneToMany(
            mappedBy = "currentCurrency",
            // cascade = CascadeType.REMOVE,
            // orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties("currentCurrency")
    private List<Group> groupsCurrent;
    
    @OneToMany(
            mappedBy = "currencyCode",
            // cascade = CascadeType.REMOVE,
            // orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties("currency")
    private List<Payment> payments;

}
