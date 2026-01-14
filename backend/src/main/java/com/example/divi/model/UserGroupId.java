package com.example.divi.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class UserGroupId implements Serializable {
//    private Long user;
//
//    private Long group;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;
    
}
