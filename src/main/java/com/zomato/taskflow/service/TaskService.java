package com.zomato.taskflow.service;

import com.zomato.taskflow.dto.GlobalApiResponse;
import com.zomato.taskflow.dto.TaskRequest;
import com.zomato.taskflow.dto.TaskResponse;
import com.zomato.taskflow.dto.TaskUpdateRequest;
import com.zomato.taskflow.entity.Project;
import com.zomato.taskflow.entity.Task;
import com.zomato.taskflow.entity.TaskStatus;
import com.zomato.taskflow.entity.User;
import com.zomato.taskflow.exception.ResourceNotFoundException;
import com.zomato.taskflow.repository.ProjectRepository;
import com.zomato.taskflow.repository.TaskRepository;
import com.zomato.taskflow.repository.UserRepository;
import com.zomato.taskflow.util.PageParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TaskService {

	private final TaskRepository taskRepository;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;

	public TaskService(
			TaskRepository taskRepository,
			ProjectRepository projectRepository,
			UserRepository userRepository) {
		this.taskRepository = taskRepository;
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public GlobalApiResponse list(UUID projectId, UUID ownerId, String status, UUID assigneeId, Integer page, Integer limit) {
		Project project = requireOwnedProject(projectId, ownerId);
		TaskStatus statusFilter = null;
		if (status != null && !status.isBlank()) {
			statusFilter = TaskStatus.fromDb(status.trim());
		}
		int pageOneBased = PageParams.pageOrDefault(page);
		int limitVal = PageParams.limitOrDefault(limit);
		var pageable = PageParams.toPageRequest(pageOneBased, limitVal, Sort.by(Sort.Direction.DESC, "createdAt"));
		var taskPage = taskRepository.findByProjectFilteredPage(project.getId(), statusFilter, assigneeId, pageable);
		List<TaskResponse> taskResponses = taskPage.getContent().stream()
				.map(TaskService::toResponse)
				.toList();

		Map<String, Object> data = new HashMap<>();
		data.put("taskResponse", taskResponses);
		data.put("pagination", paginationMap(taskPage, pageOneBased, limitVal));

		return GlobalApiResponse.success(ownerId.toString(), data);
	}

	private static Map<String, Object> paginationMap(Page<?> page, int pageOneBased, int limit) {
		Map<String, Object> m = new HashMap<>();
		m.put("page", pageOneBased);
		m.put("limit", limit);
		m.put("total", page.getTotalElements());
		m.put("total_pages", page.getTotalPages());
		return m;
	}

	@Transactional
	public GlobalApiResponse create(UUID projectId, UUID ownerId, TaskRequest request) {
		Project project = requireOwnedProject(projectId, ownerId);
		User creator = userRepository.findById(ownerId).orElseThrow();
		Task task = new Task();
		task.setTitle(request.title().trim());
		task.setDescription(blankToNull(request.description()));
		task.setStatus(request.status());
		task.setPriority(request.priority());
		task.setProject(project);
		task.setCreator(creator);
		task.setDueDate(request.dueDate());
		if (request.assigneeId() != null) {
			User assignee = userRepository.findById(request.assigneeId())
					.orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
			task.setAssignee(assignee);
		}
		taskRepository.save(task);

		Map<String, Object> data = new HashMap<>();
		data.put("taskResponse", toResponse(task));

		return GlobalApiResponse.success(ownerId.toString(), data);
	}

	@Transactional
	public GlobalApiResponse update(UUID taskId, UUID ownerId, TaskUpdateRequest request) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));
		requireOwnedProject(task.getProject().getId(), ownerId);

		if ((request.title() != null)) {
			task.setTitle(request.title());
		}

		if (request.status() != null) {
			task.setStatus(request.status());
		}

		if (request.priority() != null) {
			task.setPriority(request.priority());
		}

		task.setDescription(blankToNull(request.description()));
		task.setDueDate(request.dueDate());
		if (request.assigneeId() == null) {
			task.setAssignee(null);
		} else {
			User assignee = userRepository.findById(request.assigneeId())
					.orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
			task.setAssignee(assignee);
		}
		taskRepository.save(task);

		Map<String, Object> data = new HashMap<>();
		data.put("taskResponse", toResponse(task));

		return GlobalApiResponse.success(ownerId.toString(), data);
	}

	@Transactional
	public GlobalApiResponse delete(UUID taskId, UUID userId) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));
		assertCanDeleteTask(task, userId);
		taskRepository.delete(task);

		return GlobalApiResponse.success(userId.toString(), null);
	}

	private static void assertCanDeleteTask(Task task, UUID userId) {
		UUID projectOwnerId = task.getProject().getOwner().getId();
		UUID creatorId = task.getCreator().getId();
		if (!userId.equals(projectOwnerId) && !userId.equals(creatorId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this task");
		}
	}

	private Project requireOwnedProject(UUID projectId, UUID ownerId) {
		return projectRepository.findByIdAndOwner_Id(projectId, ownerId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private static String blankToNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		return s;
	}

	private static TaskResponse toResponse(Task t) {
		return new TaskResponse(
				t.getId(),
				t.getTitle(),
				t.getDescription(),
				t.getStatus(),
				t.getPriority(),
				t.getProject().getId(),
				t.getAssignee() != null ? t.getAssignee().getId() : null,
				t.getCreator().getId(),
				t.getDueDate(),
				t.getCreatedAt(),
				t.getUpdatedAt());
	}
}
