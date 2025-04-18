package com.jira.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Corrected import
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jira.jira.models.User;
import com.jira.jira.service.UserService;

import jakarta.validation.Valid;
@Controller
@RequestMapping("/dashboard")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("userCount", userService.countUsers());
        return "dashboard"; // Ensure it points to the correct template
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        // Check for validation errors
        if (result.hasErrors()) {
            return "add-user"; // Return to the form with error messages
        }

        // Check if username already exists
        if (userService.doesUsernameExist(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists");
            return "add-user";
        }

        // Check if email already exists
        if (userService.doesEmailExist(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            return "add-user";
        }

        // Add the user if validation passes
        userService.addUser(user);
        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "edit-user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);
        return "redirect:/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/dashboard";
    }
    // AJAX validation endpoints
    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsernameExists(
            @RequestParam String username,
            @RequestParam Long id) {
        return userService.doesUsernameExistExceptCurrent(username, id);
    }

    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmailExists(
            @RequestParam String email,
            @RequestParam Long id) {
        return userService.doesEmailExistExceptCurrent(email, id);
    }
    
}