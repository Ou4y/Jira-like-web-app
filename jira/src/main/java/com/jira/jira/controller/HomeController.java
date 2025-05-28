package com.jira.jira.controller;

import com.jira.jira.models.User;
import com.jira.jira.service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User loggedInUser = userService.getUserByUsername(username).orElse(null);
            model.addAttribute("user", loggedInUser);
            return "index";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/jira")
    public String jiraHome(Model model) {
        // Optionally, add user info to the model if needed
        return "index"; // This will render src/main/resources/templates/index.html
    }
}
