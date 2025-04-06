package com.jira.jira.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.jira.jira.models.Project;
import com.jira.jira.repositories.ProjectRepository;

@Controller
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/projects")
    public String getProjectsPage(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        return "projects";
    }

    @PostMapping("/projects/add")
    public String addProject(Project project) {
        projectRepository.save(project);
        return "redirect:/projects";
    }
}
