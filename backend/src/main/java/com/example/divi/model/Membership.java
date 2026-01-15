package com.example.divi.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "memberships")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {

    @EmbeddedId
    private UserGroupId id = new UserGroupId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("memberships")
    private User user;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    @JsonIgnoreProperties("memberships")
    private Group group;

    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }

}
