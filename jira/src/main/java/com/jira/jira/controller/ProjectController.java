package com.jira.jira.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.jira.jira.models.Project;
import com.jira.jira.models.Task;
import com.jira.jira.repositories.ProjectRepository;
import com.jira.jira.repositories.TaskRepository;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public String getProjectsPage(Model model) {
        List<Project> projects = projectRepository.findAll();
        model.addAttribute("projects", projects);
        return "projects";
    }

    @PostMapping("/add")
    public String addProject(Project project) {
        projectRepository.save(project);
        return "redirect:/projects";
    }

    @GetMapping("/view/{id}")
    public String viewProject(@PathVariable Long id, Model model) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        // Load tasks for the project
        List<Task> tasks = taskRepository.findByProjectIdOrderByStoryPointsDesc(id);
        project.setTasks(tasks);
        
        model.addAttribute("project", project);
        model.addAttribute("allStatuses", Task.Status.values());
        return "view-project";
    }
}
