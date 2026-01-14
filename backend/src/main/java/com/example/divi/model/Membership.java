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
//@IdClass(UserGroupId.class)
public class Membership {

    @EmbeddedId
    private UserGroupId id = new UserGroupId();

//    @Id
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("memberships")
    private User user;

//    @Id
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
