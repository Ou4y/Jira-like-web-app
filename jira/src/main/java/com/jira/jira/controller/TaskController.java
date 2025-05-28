package com.jira.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.jira.jira.models.Task;
import com.jira.jira.repositories.ProjectRepository;
import com.jira.jira.repositories.TaskRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Show all tasks for a given project, sorted by story points desc.
     */
    @GetMapping("/project/{projectId}")
    public ModelAndView getTasksByProject(@PathVariable Long projectId) {
        ModelAndView mav = new ModelAndView("tasks");
        mav.addObject("tasks", taskRepository.findByProjectIdOrderByStoryPointsDesc(projectId));
        mav.addObject("project", projectRepository.findById(projectId).orElse(null));
        return mav;
    }

    /**
     * View a single project along with its tasks and available statuses.
     */
    @GetMapping("/view/{projectId}")
    public ModelAndView viewProject(@PathVariable Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        var tasks = taskRepository.findByProjectIdOrderByStoryPointsDesc(projectId);
        project.setTasks(tasks);

        ModelAndView mav = new ModelAndView("view-project");
        mav.addObject("project", project);
        mav.addObject("allStatuses", Task.Status.values());
        return mav;
    }
    public ResponseEntity<?> updateTaskStatus(Long taskId, String status) {
    Optional<Task> optionalTask = taskRepository.findById(taskId);
    if (optionalTask.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    Task task = optionalTask.get();
    try {
        Task.Status newStatus = Task.Status.valueOf(status);
        task.setStatus(newStatus);
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        }
    }

    /**
     * Add a new task to a project.
     */
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

    /**
     * Show the edit form for an existing task.
     */
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

    /**
     * Process the edit form submission.
     */
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
            return "redirect:/projects/view/" + task.getProject().getId();
        }
        return "redirect:/projects";
    }

    /**
     * Delete a task.
     */
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

    /**
     * REST endpoint for updating a task’s status via drag‐and‐drop.
     * Expects JSON { "status": "TODO" | "IN_PROGRESS" | "DONE" }
     * Returns JSON { "displayName": "...", "bootstrapClass": "bg-primary" }.
     */
    @PutMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String,String>> updateTaskStatusJson(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {
        String rawStatus = body.get("status");
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        Task.Status newStatus;
        try {
            newStatus = convertToTaskStatus(rawStatus);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status value: {}", rawStatus);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value", e);
        }

        task.setStatus(newStatus);
        taskRepository.save(task);
        logger.info("Task {} status updated to {}", id, newStatus);

        Map<String,String> resp = Map.of(
            "displayName",    newStatus.getDisplayName(),
            "bootstrapClass", newStatus.getBadgeClass()
        );
        return ResponseEntity.ok(resp);
    }

    /**
     * Debug helper—inspect a task’s current status.
     */
    @GetMapping("/{id}/debug")
    @ResponseBody
    public String debugTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        return (task == null)
            ? "Not found"
            : "Task " + id + " status: " + task.getStatus();
    }

    /**
     * Normalize and convert a string to Task.Status enum.
     */
    private Task.Status convertToTaskStatus(String status) {
        try {
            String normalized = status.trim().toUpperCase().replace(" ", "_");
            return Task.Status.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status, e);
        }
    }

    /**
     * Handle status update from select dropdown and redirect back to the same page.
     */
    @PostMapping("/update-status")
    public String updateStatusFromForm(
            @RequestParam("taskId") Long taskId,
            @RequestParam("status") String status,
            HttpServletRequest request
    ) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            try {
                task.setStatus(convertToTaskStatus(status));
                taskRepository.save(task);
            } catch (IllegalArgumentException e) {
                // Optionally handle invalid status
            }
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}