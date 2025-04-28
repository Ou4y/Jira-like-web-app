package com.jira.jira.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jira.jira.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    @Override
    long count();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // New methods for edit validation
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}
