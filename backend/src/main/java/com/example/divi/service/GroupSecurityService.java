package com.example.divi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.divi.repository.MembershipRepository;

@Service("groupSecurity")
public class GroupSecurityService {

    @Autowired
    private MembershipRepository membershipRepository;

    public boolean isMember(Long groupId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return membershipRepository.existsByUser_EmailAndGroup_GroupId(email, groupId);
    }
    
}
