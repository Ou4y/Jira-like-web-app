package com.jira.jira.service;

import com.jira.jira.models.User;
import com.jira.jira.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Optional<User> getUserByUsername(String username) {
    return Optional.ofNullable(userRepository.findByUsername(username));
}

    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long countUsers() {
        return userRepository.count();
    }

    public boolean doesUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean doesEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    // New methods for edit validation
    public boolean doesUsernameExistExceptCurrent(String username, Long id) {
        return userRepository.existsByUsernameAndIdNot(username, id);
    }
    
    public boolean doesEmailExistExceptCurrent(String email, Long id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        // Only encode if password is not already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        User user = userRepository.findByUsername(username.trim());
        if (user != null && passwordEncoder.matches(password.trim(), user.getPassword())) {
            return user;
        }
        return null;
    }

    // Alternative authentication by email - direct string comparison
    public User authenticateByEmail(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}