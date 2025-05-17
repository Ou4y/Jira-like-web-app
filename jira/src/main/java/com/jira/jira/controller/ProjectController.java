package com.jira.jira.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView listProjects() {
        List<Project> projects = projectRepository.findAll();
        ModelAndView mav = new ModelAndView("projects");
        mav.addObject("projects", projects);
        return mav;
    }

    @PostMapping("/add")
    public String addProject(Project project) {
        projectRepository.save(project);
        return "redirect:/projects";
    }

    @GetMapping("/view/{id}")
    public ModelAndView viewProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<Task> tasks = taskRepository.findByProjectIdOrderByStoryPointsDesc(id);
        project.setTasks(tasks);

        ModelAndView mav = new ModelAndView("view-project");
        mav.addObject("project", project);
        mav.addObject("allStatuses", Task.Status.values());
        return mav;
    }
}

