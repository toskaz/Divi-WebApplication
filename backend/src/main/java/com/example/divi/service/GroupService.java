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
    public Group createGroup(GroupRequestDTO groupRequest ) {
        User creator = userRepository.findById(groupRequest.getCreatorId()).orElseThrow(() -> new RuntimeException("User not found"));
        Currency currency = currencyService.getCurrency(groupRequest.getCurrencyCode());

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

        return addedGroup;
    }

    private void addMemberToGroup(User user, Group group) {
        UserGroupId membershipId = new UserGroupId(user.getUserId(), group.getGroupId());

        if (!membershipRepository.existsById(membershipId)) {
            Membership membership = new Membership();
            membership.setUser(user);
            membership.setGroup(group);
            membership.setJoinedAt(LocalDateTime.now());
            membershipRepository.save(membership);
        }
    }

    public List<GroupSummaryDTO> getUserGroupsSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Membership> memberships = membershipRepository.findByUser(user);

        return memberships.stream()
                .map(membership -> {
                    Group group = membership.getGroup();

                    BigDecimal balance = balanceService.getUserBalanceInGroup(userId, group.getGroupId());

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
                })
                .collect(Collectors.toList());
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
