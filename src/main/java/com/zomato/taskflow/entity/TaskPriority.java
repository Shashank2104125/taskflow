package com.zomato.taskflow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TaskPriority {
	@JsonProperty("low")
	LOW("low"),
	@JsonProperty("medium")
	MEDIUM("medium"),
	@JsonProperty("high")
	HIGH("high");

	private final String dbValue;

	TaskPriority(String dbValue) {
		this.dbValue = dbValue;
	}

	public String getDbValue() {
		return dbValue;
	}

	public static TaskPriority fromDb(String value) {
		for (TaskPriority p : values()) {
			if (p.dbValue.equalsIgnoreCase(value)) {
				return p;
			}
		}
		throw new IllegalArgumentException("Unknown task priority: " + value);
	}
}
