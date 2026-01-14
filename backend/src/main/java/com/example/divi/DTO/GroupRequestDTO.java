package com.example.divi.DTO;

import lombok.Data;

import java.util.List;

@Data
public class GroupRequestDTO {
    private String groupName;
    private Long creatorId;
    private String currencyCode;
    private String description;
    private List<Long> memberIds;
}
