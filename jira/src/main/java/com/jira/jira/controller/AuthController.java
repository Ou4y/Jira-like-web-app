package com.jira.jira.controller;

import com.jira.jira.models.User;
import com.jira.jira.service.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // === LOGIN ===

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "success", required = false) String success,
                        @RequestParam(value = "logout", required = false) String logout) {

        if (error != null) model.addAttribute("error", "Invalid username or password");
        if (success != null) model.addAttribute("success", "Account created successfully! Please login.");
        if (logout != null) model.addAttribute("success", "You have been logged out successfully.");

        return "login";
    }
    // === SIGNUP ===

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupPost(@ModelAttribute User user, Model model) {
        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        }

        if (userService.doesEmailExist(user.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "signup";
        }

        try {
            userService.save(user);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating account. Please try again.");
            return "signup";
        }
    }

    // === LOGOUT ===

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
