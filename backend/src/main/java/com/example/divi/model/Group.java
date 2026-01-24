package com.example.divi.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "groups")
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @ManyToOne
    @JoinColumn (name="defaultCurrency")
    private Currency defaultCurrency;

    @ManyToOne
    @JoinColumn (name="currentCurrency")
    private Currency currentCurrency;

    @Column(length = 30, nullable = false)
    private String groupName;

    private LocalDateTime createdDate;
    
    @OneToMany(
            mappedBy = "group",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Membership> memberships = new ArrayList<>();
    
    @OneToMany(
            mappedBy = "group",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

}
