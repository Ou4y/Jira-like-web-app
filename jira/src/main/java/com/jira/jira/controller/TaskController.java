package com.jira.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jira.jira.models.Task;
import com.jira.jira.repositories.ProjectRepository;
import com.jira.jira.repositories.TaskRepository;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/project/{projectId}")
    public String getTasksByProject(@PathVariable Long projectId, Model model) {
        model.addAttribute("tasks", taskRepository.findByProjectId(projectId));
        model.addAttribute("project", projectRepository.findById(projectId).orElse(null));
        return "tasks";
    }

    @GetMapping("/view/{projectId}")
    public String viewProject(@PathVariable Long projectId, Model model) {
        var project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            System.out.println("Project not found for ID: " + projectId);
            return "error/404"; // Return a 404 error page if the project is not found
        }
        System.out.println("Project found: " + project.getName());
        model.addAttribute("project", project);
        return "view-project"; // Ensure this matches the template name
    }

    @PostMapping("/add")
    public String addTask(@RequestParam Long projectId, @RequestParam int storyPoints, Task task) {
        task.setProject(projectRepository.findById(projectId).orElse(null));
        task.setStoryPoints(storyPoints);
        taskRepository.save(task);
        return "redirect:/projects/view/" + projectId;
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id).orElse(null);
        model.addAttribute("task", task);
        return "edit-task";
    }

    @PostMapping("/edit/{id}")
    public String editTask(@PathVariable Long id, @RequestParam int storyPoints, Task updatedTask) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setStoryPoints(storyPoints);
            taskRepository.save(task);
        }
        return "redirect:/projects/view/" + task.getProject().getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            Long projectId = task.getProject().getId();
            taskRepository.delete(task);
            return "redirect:/projects/view/" + projectId;
        }
        return "redirect:/projects";
    }
}
