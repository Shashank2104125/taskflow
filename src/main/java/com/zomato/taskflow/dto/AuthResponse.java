package com.zomato.taskflow.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds, UserSummaryResponse user) {
}
