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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_id")
    private Long groupId;
    
}
