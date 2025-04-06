package com.jira.jira.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jira.jira.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    long count(); // This method counts all users in the database
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
