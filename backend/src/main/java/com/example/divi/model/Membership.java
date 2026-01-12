package com.example.divi.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserGroupId.class)
public class Membership {

    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnoreProperties("memberships")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "groupId")
    @JsonIgnoreProperties("memberships")
    private Group group;

    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }

}
