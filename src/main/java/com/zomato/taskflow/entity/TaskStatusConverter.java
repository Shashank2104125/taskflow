package com.zomato.taskflow.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskStatusConverter implements AttributeConverter<TaskStatus, String> {

	@Override
	public String convertToDatabaseColumn(TaskStatus attribute) {
		return attribute == null ? null : attribute.getDbValue();
	}

	@Override
	public TaskStatus convertToEntityAttribute(String dbData) {
		return dbData == null ? null : TaskStatus.fromDb(dbData);
	}
}
