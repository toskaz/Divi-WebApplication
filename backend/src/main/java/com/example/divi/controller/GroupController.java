package com.example.divi.controller;

import com.example.divi.DTO.*;
import com.example.divi.service.BalanceService;
import com.example.divi.service.ExpenseService;
import com.example.divi.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:5173")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<GroupSummaryDTO> createGroup(@RequestBody GroupRequestDTO groupRequest) {
        GroupSummaryDTO addedGroup = groupService.createGroup(groupRequest);
        return ResponseEntity.ok(addedGroup);
    }

    @GetMapping("/{groupId}/balances")
    public ResponseEntity<List<BalanceDTO>> getGroupBalances(@PathVariable Long groupId) {
        return ResponseEntity.ok(balanceService.calculateGroupBalances(groupId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<GroupSummaryDTO>> getMyGroups() {
        return ResponseEntity.ok(groupService.getUserGroupsSummary());
    }

    @GetMapping("/details/{groupId}")
    public ResponseEntity<GroupDetailsDTO> getGroupDetails(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupDetails(groupId));
    }

    @GetMapping("/{groupId}/settlements")
    public ResponseEntity<List<SettlementDTO>> getGroupSettlements(@PathVariable Long groupId) {
        List<SettlementDTO> plan = balanceService.getSettlementPlan(groupId);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/{groupId}/settle")
    public ResponseEntity<String> settleDebt(@PathVariable Long groupId, @RequestBody SettlementDTO settlementData) {
        expenseService.settleDebt(groupId, settlementData.getFromUserId(), settlementData.getToUserId(), settlementData.getAmount());
        return ResponseEntity.ok("Settlement recorded");
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok("Group deleted successfully");
    }



}
