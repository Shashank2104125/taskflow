package com.zomato.taskflow.controller;

import com.zomato.taskflow.dto.GlobalApiResponse;
import com.zomato.taskflow.dto.TaskRequest;
import com.zomato.taskflow.dto.TaskUpdateRequest;
import com.zomato.taskflow.security.AppUserDetails;
import com.zomato.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TaskController {

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping("/projects/{projectId}/tasks")
	public GlobalApiResponse list(
			@AuthenticationPrincipal AppUserDetails principal,
			@PathVariable UUID projectId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) UUID assignee,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		return taskService.list(projectId, principal.getId(), status, assignee, page, limit);
	}

	@PostMapping("/projects/{projectId}/tasks")
	@ResponseStatus(HttpStatus.CREATED)
	public GlobalApiResponse create(
			@AuthenticationPrincipal AppUserDetails principal,
			@PathVariable UUID projectId,
			@Valid @RequestBody TaskRequest request) {
		return taskService.create(projectId, principal.getId(), request);
	}

	@PatchMapping("/tasks/{taskId}")
	public GlobalApiResponse update(
			@AuthenticationPrincipal AppUserDetails principal,
			@PathVariable UUID taskId,
			@Valid @RequestBody TaskUpdateRequest request) {
		return taskService.update(taskId, principal.getId(), request);
	}

	@DeleteMapping("/tasks/{taskId}")
	public GlobalApiResponse delete(
			@AuthenticationPrincipal AppUserDetails principal,
			@PathVariable UUID taskId) {
		return taskService.delete(taskId, principal.getId());
	}
}
