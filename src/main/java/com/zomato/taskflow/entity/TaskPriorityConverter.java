package com.zomato.taskflow.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskPriorityConverter implements AttributeConverter<TaskPriority, String> {

	@Override
	public String convertToDatabaseColumn(TaskPriority attribute) {
		return attribute == null ? null : attribute.getDbValue();
	}

	@Override
	public TaskPriority convertToEntityAttribute(String dbData) {
		return dbData == null ? null : TaskPriority.fromDb(dbData);
	}
}
