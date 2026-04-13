package com.zomato.taskflow.controller;

import com.zomato.taskflow.dto.GlobalApiResponse;
import com.zomato.taskflow.dto.ProjectRequest;
import com.zomato.taskflow.security.AppUserDetails;
import com.zomato.taskflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GetMapping
	public GlobalApiResponse list(
			@AuthenticationPrincipal AppUserDetails principal,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		return projectService.listForOwner(principal.getId(), page, limit);
	}

	@GetMapping("/{projectId}/stats")
	public GlobalApiResponse stats(
			@AuthenticationPrincipal AppUserDetails principal,
			@PathVariable UUID projectId) {
		return projectService.stats(projectId, principal.getId());
	}

	@GetMapping("/{projectId}")
	public GlobalApiResponse get(
			@AuthenticationPrincipal AppUserDetails principal,
			@PathVariable UUID projectId) {
		return projectService.get(projectId, principal.getId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GlobalApiResponse create(
			@AuthenticationPrincipal AppUserDetails principal,
			@Valid @RequestBody ProjectRequest request) {
		return projectService.create(principal.getId(), request);
	}

	@PatchMapping("/{projectId}")
	public GlobalApiResponse update(
			@AuthenticationPrincipal AppUserDetails principal,
			@PathVariable UUID projectId,
			@Valid @RequestBody ProjectRequest request) {
		return projectService.update(projectId, principal.getId(), request);
	}

	@DeleteMapping("/{projectId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public GlobalApiResponse delete(
			@AuthenticationPrincipal AppUserDetails principal,
			@PathVariable UUID projectId) {
		return projectService.delete(projectId, principal.getId());
	}
}
