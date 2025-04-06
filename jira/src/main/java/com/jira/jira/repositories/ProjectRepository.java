package com.jira.jira.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jira.jira.models.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}