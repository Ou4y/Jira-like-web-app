package com.jira.jira.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jira.jira.models.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
}
