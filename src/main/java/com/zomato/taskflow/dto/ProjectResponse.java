package com.zomato.taskflow.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ProjectResponse(UUID id, String name, String description, UUID ownerId, Instant createdAt, List<Object> tasks) {
}
