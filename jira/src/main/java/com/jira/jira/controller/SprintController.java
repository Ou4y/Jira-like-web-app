package com.jira.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.jira.jira.models.Project;
import com.jira.jira.models.Sprint;
import com.jira.jira.repositories.ProjectRepository;
import com.jira.jira.repositories.SprintRepository;

@Controller
@RequestMapping("/sprints")
public class SprintController {

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/details/{id}")
    public ModelAndView getSprintDetails(@PathVariable Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ModelAndView mav = new ModelAndView("sprint-details");
        mav.addObject("sprint", sprint);
        return mav;
    }

    @PostMapping("/add")
    public String addSprint(@RequestParam Long projectId, @ModelAttribute Sprint sprint) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sprint.setProject(project);
        sprintRepository.save(sprint);
        return "redirect:/projects/view/" + projectId;
    }

    @PostMapping("/add-task")
    public String addTaskToSprint(
            @RequestParam Long sprintId,
            @RequestParam Long taskId
    ) {
        var sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var task = sprint.getProject().getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);
        if (task != null) {
            task.setSprint(sprint);
            // Optionally, update status or other fields here
            sprint.getSprintBacklog().add(task);
            sprintRepository.save(sprint);
        }
        // Redirect to the sprint details page
        return "redirect:/sprints/details/" + sprintId;
    }
}