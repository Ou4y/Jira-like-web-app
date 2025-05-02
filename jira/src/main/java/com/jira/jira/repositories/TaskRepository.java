package com.jira.jira.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jira.jira.models.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectIdOrderByStoryPointsDesc(Long projectId);

    @Query("SELECT t FROM Task t JOIN FETCH t.project WHERE t.id = :id")
    Task findByIdWithProject(@Param("id") Long id);
}
