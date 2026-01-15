package com.example.divi.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "groups")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @ManyToOne
    @JoinColumn (name="defaultCurrency")
    @JsonIgnoreProperties("groupsDefault")
    private Currency defaultCurrency;

    @ManyToOne
    @JoinColumn (name="currentCurrency")
    @JsonIgnoreProperties("groupsCurrent")
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
    @JsonIgnoreProperties("group")
    private List<Membership> memberships;
    
    @OneToMany(
            mappedBy = "group",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties("group")
    private List<Payment> payments;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

}
