package com.example.divi.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "currencies")
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
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Group> groupsDefault;

    @OneToMany(
            mappedBy = "currentCurrency",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Group> groupsCurrent;
    
    @OneToMany(
            mappedBy = "currencyCode",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Payment> payments;

}
