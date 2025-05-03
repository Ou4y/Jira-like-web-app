package com.jira.jira.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jira.jira.models.Sprint;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
}