package com.example.divi.service;

import com.example.divi.DTO.GroupRequestDTO;
import com.example.divi.model.*;
import com.example.divi.repository.GroupRepository;
import com.example.divi.repository.MembershipRepository;
import com.example.divi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

}
