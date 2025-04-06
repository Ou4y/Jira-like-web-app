package com.jira.jira.service;

import com.jira.jira.models.User;
import com.jira.jira.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
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
}