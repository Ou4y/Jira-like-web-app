package com.jira.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jira.jira.models.User;
import com.jira.jira.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/dashboard")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ModelAndView getAllUsers() {
        ModelAndView mav = new ModelAndView("dashboard");
        mav.addObject("users", userService.getAllUsers());
        mav.addObject("userCount", userService.countUsers());
        return mav;
    }

    @GetMapping("/add")
    public ModelAndView showAddUserForm() {
        ModelAndView mav = new ModelAndView("add-user");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }
        if (userService.doesUsernameExist(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists");
            return "add-user";
        }
        if (userService.doesEmailExist(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            return "add-user";
        }
        userService.addUser(user);
        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditUserForm(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("edit-user");
        mav.addObject("user", userService.getUserById(id));
        return mav;
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