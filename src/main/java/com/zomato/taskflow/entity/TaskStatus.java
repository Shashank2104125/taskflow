package com.zomato.taskflow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TaskStatus {
	@JsonProperty("todo")
	TODO("todo"),
	@JsonProperty("in_progress")
	IN_PROGRESS("in_progress"),
	@JsonProperty("done")
	DONE("done");

	private final String dbValue;

	TaskStatus(String dbValue) {
		this.dbValue = dbValue;
	}

	public String getDbValue() {
		return dbValue;
	}

	public static TaskStatus fromDb(String value) {
		for (TaskStatus s : values()) {
			if (s.dbValue.equalsIgnoreCase(value)) {
				return s;
			}
		}
		throw new IllegalArgumentException("Unknown task status: " + value);
	}
}
