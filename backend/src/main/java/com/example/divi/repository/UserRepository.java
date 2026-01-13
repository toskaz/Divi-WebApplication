package com.example.divi.repository;

import com.example.divi.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String email);
}
