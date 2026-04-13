package com.zomato.taskflow.dto;

import com.zomato.taskflow.entity.TaskPriority;
import com.zomato.taskflow.entity.TaskStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record TaskUpdateRequest(
        @Size(max = 500) String title,
        @Size(max = 50_000) String description,
        TaskStatus status,
        TaskPriority priority,
        UUID assigneeId,
        LocalDate dueDate
) {
}