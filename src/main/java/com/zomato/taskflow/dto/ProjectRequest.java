package com.zomato.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
		@NotBlank @Size(max = 255) String name,
		@Size(max = 10_000) String description
) {
}
