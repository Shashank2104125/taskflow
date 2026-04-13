package com.zomato.taskflow.dto;

import java.util.List;
import java.util.Map;

public record ProjectStatsResponse(Map<String, Long> byStatus, List<AssigneeTaskCount> byAssignee) {
}
