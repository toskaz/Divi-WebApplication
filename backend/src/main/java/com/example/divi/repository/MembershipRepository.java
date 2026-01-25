package com.example.divi.repository;

import com.example.divi.model.Group;
import com.example.divi.model.Membership;
import com.example.divi.model.User;
import com.example.divi.model.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UserGroupId> {
    List<Membership> findByUser(User user);
    List<Membership> findByUser_UserId(Long userId);
    List<Membership> findByGroup(Group group);
    List<Membership> findByGroup_GroupId(Long groupId);
    Optional<Membership> findByUserAndGroup(User user, Group group);
    boolean existsByUser_UserIdAndGroup_GroupId(Long userId, Long groupId);
}
