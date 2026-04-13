package com.zomato.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GlobalApiResponse(
		String uid,
		String status,
		String reasonCode,
		String message,
		String error,
		Map<String, Object> data) {

	public static GlobalApiResponse success(String uid, Map<String, Object> data) {
		Map<String, Object> payload = data != null ? new LinkedHashMap<>(data) : new LinkedHashMap<>();
		return new GlobalApiResponse(uid, "SUCCESS", null, "OK", null, Collections.unmodifiableMap(payload));
	}

	public static GlobalApiResponse failure(String uid, String reasonCode, String message, String error, Map<String, Object> data) {
		Map<String, Object> payload = data != null ? new LinkedHashMap<>(data) : new LinkedHashMap<>();
		return new GlobalApiResponse(uid, "FAILED", reasonCode, message, error, Collections.unmodifiableMap(payload));
	}
}
