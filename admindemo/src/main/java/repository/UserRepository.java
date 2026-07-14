package com.example.admindemo.repository;

// Feature Branch: Payment - Added for Git branching workflow demo

import com.example.admindemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}