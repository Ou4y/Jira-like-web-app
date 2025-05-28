package com.jira.jira.controller;

import com.jira.jira.models.Task;
import com.jira.jira.controller.TaskController;
import com.jira.jira.models.Project;
import com.jira.jira.repositories.ProjectRepository;
import com.jira.jira.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTasksByProject_ShouldReturnTasksAndProject() {
        Long projectId = 1L;
        List<Task> tasks = List.of(new Task());
        Project project = new Project();

        when(taskRepository.findByProjectIdOrderByStoryPointsDesc(projectId)).thenReturn(tasks);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ModelAndView mav = taskController.getTasksByProject(projectId);

        assertEquals("tasks", mav.getViewName());
        assertEquals(tasks, mav.getModel().get("tasks"));
        assertEquals(project, mav.getModel().get("project"));
    }

    @Test
    void viewProject_ShouldReturnModelAndView() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        List<Task> tasks = List.of(new Task());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectIdOrderByStoryPointsDesc(projectId)).thenReturn(tasks);

        ModelAndView mav = taskController.viewProject(projectId);

        assertEquals("view-project", mav.getViewName());
        assertEquals(project, mav.getModel().get("project"));
        assertNotNull(mav.getModel().get("allStatuses"));
    }

    @Test
    void addTask_ShouldSaveTaskAndRedirect() {
        Long projectId = 1L;
        String status = "TODO";
        int storyPoints = 5;
        Project project = new Project();
        Task task = new Task();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        String result = taskController.addTask(projectId, status, storyPoints, task);

        verify(taskRepository).save(task);
        assertEquals("redirect:/projects/view/" + projectId, result);
        assertEquals(Task.Status.TODO, task.getStatus());
        assertEquals(storyPoints, task.getStoryPoints());
        assertEquals(project, task.getProject());
    }

    @Test
    void editTaskForm_ShouldReturnEditViewIfTaskExists() {
        Long id = 1L;
        Task task = new Task();
        Project project = new Project();
        task.setProject(project);

        when(taskRepository.findByIdWithProject(id)).thenReturn(task);

        ModelAndView mav = taskController.editTaskForm(id);

        assertEquals("edit-task", mav.getViewName());
        assertEquals(task, mav.getModel().get("task"));
    }

    @Test
    void editTask_ShouldUpdateAndRedirect() {
        Long id = 1L;
        Task existingTask = new Task();
        existingTask.setProject(new Project());
        existingTask.getProject().setId(10L);

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated");
        updatedTask.setDescription("Updated Desc");
        updatedTask.setStatus(Task.Status.TODO);
        updatedTask.setStoryPoints(8);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existingTask));

        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        String result = taskController.editTask(id, updatedTask, bindingResult);

        verify(taskRepository).save(existingTask);
        assertEquals("redirect:/projects/view/10", result);
    }

    @Test
    void deleteTask_ShouldDeleteAndRedirect() {
        Long taskId = 1L;
        Project project = new Project();
        project.setId(123L);
        Task task = new Task();
        task.setProject(project);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        String result = taskController.deleteTask(taskId);

        verify(taskRepository).delete(task);
        assertEquals("redirect:/projects/view/123", result);
    }

    @Test
    void updateTaskStatus_ShouldReturnOkWhenValid() {
        Long taskId = 1L;
        Task task = new Task();
        task.setStatus(Task.Status.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ResponseEntity<?> response = taskController.updateTaskStatus(taskId, "IN_PROGRESS");

        verify(taskRepository).save(task);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void updateTaskStatus_ShouldReturnBadRequestForInvalidStatus() {
        Long taskId = 1L;
        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        ResponseEntity<?> response = taskController.updateTaskStatus(taskId, "INVALID");

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Invalid status"));
    }
}