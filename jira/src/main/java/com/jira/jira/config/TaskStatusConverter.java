package com.jira.jira.config;

import com.jira.jira.models.Task.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusConverter implements Converter<String, Status> {
    @SuppressWarnings("null")
    @Override
    public Status convert(String source) {
        for (Status status : Status.values()) {
            if (status.getDisplayName().equalsIgnoreCase(source)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + source);
    }
}
