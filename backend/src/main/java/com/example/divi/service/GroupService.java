package com.example.divi.service;

import com.example.divi.DTO.GroupDetailsDTO;
import com.example.divi.DTO.GroupMemberDTO;
import com.example.divi.DTO.GroupRequestDTO;
import com.example.divi.DTO.GroupSummaryDTO;
import com.example.divi.model.*;
import com.example.divi.repository.GroupRepository;
import com.example.divi.repository.MembershipRepository;
import com.example.divi.repository.PaymentRepository;
import com.example.divi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private PaymentRepository paymentRepository;


    @Transactional
    public GroupSummaryDTO createGroup(GroupRequestDTO groupRequest) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User creator = userRepository.findByEmail(currentEmail)
            .orElseThrow(() -> new RuntimeException("Creator with email '" + currentEmail + "' not found"));

        Currency currency = currencyService.getCurrency(groupRequest.getCurrencyCode());

        if (currency == null) {
            throw new RuntimeException("Currency with code '" + groupRequest.getCurrencyCode() + "' not found");
        }

        Group group = new Group();
        group.setGroupName(groupRequest.getGroupName());
        group.setDefaultCurrency(currency);
        group.setCurrentCurrency(currency);
        Group addedGroup = groupRepository.save(group);

        addMemberToGroup(creator, addedGroup);

        if (groupRequest.getMembers() != null) {
            for (GroupMemberDTO groupMemberDTO : groupRequest.getMembers()) {
                String email = groupMemberDTO.getEmail();
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User with email '" + email + "' not found"));
                addMemberToGroup(user, addedGroup);
            }
        }

        return convertToSummaryDTO(addedGroup, creator);
    }

    private void addMemberToGroup(User user, Group group) {
        UserGroupId membershipId = new UserGroupId(user.getUserId(), group.getGroupId());

        if (!membershipRepository.existsById(membershipId)) {
            Membership membership = new Membership();
            membership.setUser(user);
            membership.setGroup(group);
            membershipRepository.save(membership);

            group.getMemberships().add(membership);
        }
    }

    public List<GroupSummaryDTO> getUserGroupsSummary() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email '" + email + "' not found"));

        List<Membership> memberships = membershipRepository.findByUser(currentUser);

        return memberships.stream()
                .map(membership -> convertToSummaryDTO(membership.getGroup(), currentUser))
                .collect(Collectors.toList());
    }

    private GroupSummaryDTO convertToSummaryDTO(Group group, User user) {
        BigDecimal balance = balanceService.getUserBalanceInGroup(user.getUserId(), group.getGroupId());

        int memberCount = group.getMemberships() != null ? group.getMemberships().size() : 0;

        LocalDateTime lastPayment = null;
        if (group.getPayments() != null && !group.getPayments().isEmpty()) {
            lastPayment = group.getPayments().stream()
                    .map(Payment::getDate)
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }

        return new GroupSummaryDTO(
                group.getGroupId(),
                group.getGroupName(),
                group.getDefaultCurrency().getCurrencyCode(),
                balance,
                lastPayment,
                memberCount
        );
    }

    public GroupDetailsDTO getGroupDetails(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupDetailsDTO groupDetailsDTO = new GroupDetailsDTO();
        groupDetailsDTO.setGroupId(group.getGroupId());
        groupDetailsDTO.setGroupName(group.getGroupName());
        groupDetailsDTO.setCurrencyCode(group.getDefaultCurrency().getCurrencyCode());

        groupDetailsDTO.setMemberCount(group.getMemberships() != null ? group.getMemberships().size() : 0);

        if (group.getPayments() != null) {
            groupDetailsDTO.setExpenseCount(group.getPayments().size());

            BigDecimal total = group.getPayments().stream()
                    .map(Payment::getDefaultCurrencyAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            groupDetailsDTO.setTotalExpenses(total);
        } else {
            groupDetailsDTO.setExpenseCount(0);
            groupDetailsDTO.setTotalExpenses(BigDecimal.ZERO);
        }

        return groupDetailsDTO;
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        List<Payment> payments = paymentRepository.findByGroup_GroupIdOrderByDateDesc(groupId);
        paymentRepository.deleteAll(payments);

        List<Membership> memberships = membershipRepository.findByGroup(group);
        membershipRepository.deleteAll(memberships);

        groupRepository.delete(group);
    }


}
