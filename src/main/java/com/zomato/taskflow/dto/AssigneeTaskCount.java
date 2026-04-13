package com.zomato.taskflow.dto;

import java.util.UUID;

public record AssigneeTaskCount(UUID assigneeId, long count) {
}
