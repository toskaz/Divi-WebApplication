package com.example.divi.controller;

import com.example.divi.DTO.*;
import com.example.divi.service.BalanceService;
import com.example.divi.service.ExpenseService;
import com.example.divi.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("@groupSecurity.isMember(#groupId)")
    @GetMapping("/{groupId}/balances")
    public ResponseEntity<List<BalanceDTO>> getGroupBalances(@PathVariable Long groupId) {
        return ResponseEntity.ok(balanceService.calculateGroupBalances(groupId));
    }

    @PreAuthorize("@groupSecurity.isMember(#groupId)")
    @GetMapping("/details/{groupId}")
    public ResponseEntity<GroupDetailsDTO> getGroupDetails(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupDetails(groupId));
    }

    @PreAuthorize("@groupSecurity.isMember(#groupId)")
    @GetMapping("/{groupId}/settlements")
    public ResponseEntity<List<SettlementDTO>> getGroupSettlements(@PathVariable Long groupId) {
        List<SettlementDTO> plan = balanceService.getSettlementPlan(groupId);
        return ResponseEntity.ok(plan);
    }

    @PreAuthorize("@groupSecurity.isMember(#groupId)")
    @PostMapping("/{groupId}/settle")
    public ResponseEntity<String> settleDebt(@PathVariable Long groupId, @RequestBody SettlementDTO settlementData) {
        expenseService.settleDebt(groupId, settlementData.getFromUserId(), settlementData.getToUserId(), settlementData.getAmount());
        return ResponseEntity.ok("Settlement recorded");
    }

    @PreAuthorize("@groupSecurity.isMember(#groupId)")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok("Group deleted successfully");
    }

    @PreAuthorize("@groupSecurity.isMember(#groupId)")
    @GetMapping("/{groupId}/expense-context")
    public ResponseEntity<ExpenseContextDTO> getExpenseContext(@PathVariable Long groupId) {
        ExpenseContextDTO expenseContext = groupService.getExpenseContext(groupId);
        return ResponseEntity.ok(expenseContext);
    }

    @GetMapping("/me")
    public ResponseEntity<List<GroupSummaryDTO>> getMyGroups() {
        return ResponseEntity.ok(groupService.getUserGroupsSummary());
    }

}
