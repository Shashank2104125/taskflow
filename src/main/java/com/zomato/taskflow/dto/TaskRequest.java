package com.zomato.taskflow.dto;

import com.zomato.taskflow.entity.TaskPriority;
import com.zomato.taskflow.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record TaskRequest(
		@NotBlank @Size(max = 500) String title,
		@Size(max = 50_000) String description,
		@NotNull TaskStatus status,
		@NotNull TaskPriority priority,
		UUID assigneeId,
		LocalDate dueDate
) {
}
