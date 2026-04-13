package com.zomato.taskflow.dto;

import java.util.UUID;

public record UserSummaryResponse(UUID id, String name, String email) {
}
