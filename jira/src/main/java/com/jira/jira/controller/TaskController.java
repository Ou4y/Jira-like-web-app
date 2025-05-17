package com.jira.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.jira.jira.models.Task;
import com.jira.jira.repositories.ProjectRepository;
import com.jira.jira.repositories.TaskRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/project/{projectId}")
    public ModelAndView getTasksByProject(@PathVariable Long projectId) {
        ModelAndView mav = new ModelAndView("tasks");
        mav.addObject("tasks", taskRepository.findByProjectIdOrderByStoryPointsDesc(projectId));
        mav.addObject("project", projectRepository.findById(projectId).orElse(null));
        return mav;
    }

    @GetMapping("/view/{projectId}")
    public ModelAndView viewProject(@PathVariable Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var tasks = taskRepository.findByProjectIdOrderByStoryPointsDesc(projectId);
        project.setTasks(tasks);

        ModelAndView mav = new ModelAndView("view-project");
        mav.addObject("project", project);
        mav.addObject("allStatuses", Task.Status.values());
        return mav;
    }

    @PostMapping("/add")
    public String addTask(
        @RequestParam Long projectId,
        @RequestParam String status,
        @RequestParam int storyPoints,
        Task task
    ) {
        try {
            task.setStatus(convertToTaskStatus(status));
        } catch (IllegalArgumentException e) {
            return "redirect:/projects/view/" + projectId + "?error=invalid_status";
        }
        task.setProject(projectRepository.findById(projectId).orElse(null));
        task.setStoryPoints(storyPoints);
        taskRepository.save(task);
        return "redirect:/projects/view/" + projectId;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editTaskForm(@PathVariable Long id) {
        Task task = taskRepository.findByIdWithProject(id);
        ModelAndView mav = new ModelAndView("edit-task");
        if (task != null) {
            mav.addObject("task", task);
            mav.addObject("project", task.getProject());
            mav.addObject("allStatuses", Task.Status.values());
        }
        return mav;
    }

    @PostMapping("/edit/{id}")
    public String editTask(
        @PathVariable Long id,
        @Valid @ModelAttribute("task") Task updatedTask,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "edit-task";
        }
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            try {
                task.setStatus(convertToTaskStatus(updatedTask.getStatus().toString()));
            } catch (IllegalArgumentException e) {
                return "redirect:/projects/view/" + task.getProject().getId() + "?error=invalid_status";
            }
            task.setStoryPoints(updatedTask.getStoryPoints());
            taskRepository.save(task);
            if (task.getProject() != null) {
                return "redirect:/projects/view/" + task.getProject().getId();
            }
        }
        return "redirect:/projects";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getProject() != null) {
            Long projectId = task.getProject().getId();
            taskRepository.delete(task);
            return "redirect:/projects/view/" + projectId;
        }
        return "redirect:/projects";
    }

    @PostMapping("/update-status/{taskId}")
    @ResponseBody
    public ResponseEntity<?> updateTaskStatus(
        @PathVariable Long taskId,
        @RequestParam String newStatus
    ) {
        try {
            Task task = taskRepository.findById(taskId).orElse(null);
            if (task != null) {
                task.setStatus(convertToTaskStatus(newStatus));
                taskRepository.save(task);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }
    }

    private Task.Status convertToTaskStatus(String status) throws IllegalArgumentException {
        String normalized = status.trim().toUpperCase().replace(" ", "_");
        try {
            return Task.Status.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + normalized, e);
        }
    }
}