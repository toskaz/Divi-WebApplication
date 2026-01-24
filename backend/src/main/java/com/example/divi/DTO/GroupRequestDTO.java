package com.example.divi.DTO;

import lombok.Data;

import java.util.List;

@Data
public class GroupRequestDTO {
    private String groupName;
    private String currencyCode;
    private List<GroupMemberDTO> members;
}
