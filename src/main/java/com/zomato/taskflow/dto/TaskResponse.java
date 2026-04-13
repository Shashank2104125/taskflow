package com.zomato.taskflow.dto;

import com.zomato.taskflow.entity.TaskPriority;
import com.zomato.taskflow.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
		UUID id,
		String title,
		String description,
		TaskStatus status,
		TaskPriority priority,
		UUID projectId,
		UUID assigneeId,
		UUID creatorId,
		LocalDate dueDate,
		Instant createdAt,
		Instant updatedAt
) {
}
