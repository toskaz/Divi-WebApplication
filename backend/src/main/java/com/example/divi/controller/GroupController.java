package com.example.divi.controller;

import com.example.divi.DTO.GroupRequestDTO;
import com.example.divi.model.Group;
import com.example.divi.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:5173")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody GroupRequestDTO groupRequest) {
        Group addedGroup = groupService.createGroup(groupRequest);
        return ResponseEntity.ok(addedGroup);
    }
}
