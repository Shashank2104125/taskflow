package com.zomato.taskflow.dto;

import java.util.Map;

public record ErrorBody(String error, Map<String, String> fieldErrors) {

	public static ErrorBody of(String error) {
		return new ErrorBody(error, null);
	}

	public static ErrorBody withFields(String error, Map<String, String> fieldErrors) {
		return new ErrorBody(error, fieldErrors);
	}
}
