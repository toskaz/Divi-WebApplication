package com.example.divi.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 100)
    private String fullName;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    private LocalDateTime registeredDate;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Membership> memberships = new ArrayList<>();
    
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Split> splits = new ArrayList<>();
    
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.registeredDate = LocalDateTime.now();
    }

}
