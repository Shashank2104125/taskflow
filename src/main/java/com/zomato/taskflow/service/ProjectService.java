package com.zomato.taskflow.service;

import com.zomato.taskflow.dto.*;
import com.zomato.taskflow.entity.Project;
import com.zomato.taskflow.entity.Task;
import com.zomato.taskflow.entity.TaskStatus;
import com.zomato.taskflow.entity.User;
import com.zomato.taskflow.exception.ResourceNotFoundException;
import com.zomato.taskflow.repository.ProjectRepository;
import com.zomato.taskflow.repository.TaskRepository;
import com.zomato.taskflow.repository.UserRepository;
import com.zomato.taskflow.util.PageParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	private final TaskRepository taskRepository;

	public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, TaskRepository taskRepository) {
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
		this.taskRepository = taskRepository;
	}

	@Transactional(readOnly = true)
	public GlobalApiResponse listForOwner(UUID ownerId, Integer page, Integer limit) {
		log.info("listForOwner called ownerId={}, page={}, limit={}", ownerId, page, limit);
		try {
			int pageOneBased = PageParams.pageOrDefault(page);
			int limitVal = PageParams.limitOrDefault(limit);

			var pageable = PageParams.toPageRequest(pageOneBased, limitVal,
					Sort.by(Sort.Direction.DESC, "createdAt"));

			Page<Project> projectPage =
					projectRepository.findAllOwnedOrWithTasksForUser(ownerId, pageable);

			List<ProjectResponse> projectResponses = projectPage.getContent().stream()
					.map(ProjectService::toResponse)
					.toList();

			Map<String, Object> data = new HashMap<>();
			data.put("projects", projectResponses);
			data.put("pagination", paginationMap(projectPage, pageOneBased, limitVal));

			log.info("listForOwner success ownerId={}, returnedProjects={}, total={}",
					ownerId, projectResponses.size(), projectPage.getTotalElements());

			return GlobalApiResponse.success(ownerId.toString(), data);

		} catch (Exception e) {
			log.error("listForOwner failed ownerId={}, page={}, limit={}", ownerId, page, limit, e);
			throw e;
		}
	}

	@Transactional(readOnly = true)
	public GlobalApiResponse stats(UUID projectId, UUID ownerId) {
		log.info("stats called projectId={}, ownerId={}", projectId, ownerId);
		try {
			Project project = projectRepository.findByIdAndOwner_Id(projectId, ownerId)
					.orElseThrow(() -> {
						log.warn("stats project not found projectId={}, ownerId={}", projectId, ownerId);
						return new ResourceNotFoundException("Project not found");
					});

			Map<String, Long> byStatus = new LinkedHashMap<>();
			for (TaskStatus s : TaskStatus.values()) {
				byStatus.put(s.getDbValue(), 0L);
			}

			for (Object[] row : taskRepository.countGroupedByStatus(project.getId())) {
				TaskStatus status = (TaskStatus) row[0];
				long count = (Long) row[1];
				byStatus.put(status.getDbValue(), count);
			}

			List<AssigneeTaskCount> byAssignee = new ArrayList<>();
			for (Object[] row : taskRepository.countGroupedByAssignee(project.getId())) {
				UUID assigneeId = (UUID) row[0];
				long count = (Long) row[1];
				byAssignee.add(new AssigneeTaskCount(assigneeId, count));
			}

			byAssignee.sort(Comparator.comparing(
					AssigneeTaskCount::assigneeId,
					Comparator.nullsFirst(Comparator.naturalOrder())
			));

			Map<String, Object> data = new HashMap<>();
			data.put("stats", new ProjectStatsResponse(byStatus, List.copyOf(byAssignee)));

			log.info("stats success projectId={}, statusCount={}, assigneeCount={}",
					projectId, byStatus.size(), byAssignee.size());

			return GlobalApiResponse.success(ownerId.toString(), data);

		} catch (Exception e) {
			log.error("stats failed projectId={}, ownerId={}", projectId, ownerId, e);
			throw e;
		}
	}

	@Transactional(readOnly = true)
	public GlobalApiResponse get(UUID projectId, UUID ownerId) {
		log.info("get project called projectId={}, ownerId={}", projectId, ownerId);
		try {
			Project project = projectRepository.findByIdAndOwner_Id(projectId, ownerId)
					.orElseThrow(() -> {
						log.warn("get project not found projectId={}, ownerId={}", projectId, ownerId);
						return new ResourceNotFoundException("Project not found");
					});

			List<TaskResponse> taskResponses =
					taskRepository.findAllTasksByProject_IdOrderByCreatedAtDesc(project.getId())
							.stream()
							.map(ProjectService::toTaskResponse)
							.toList();

			ProjectResponse projectResponse = toResponse(project);
			projectResponse.tasks().addAll(taskResponses);

			Map<String, Object> data = new HashMap<>();
			data.put("project", projectResponse);

			log.info("get project success projectId={}, tasksCount={}", projectId, taskResponses.size());

			return GlobalApiResponse.success(ownerId.toString(), data);

		} catch (Exception e) {
			log.error("get project failed projectId={}, ownerId={}", projectId, ownerId, e);
			throw e;
		}
	}

	@Transactional
	public GlobalApiResponse create(UUID ownerId, ProjectRequest request) {
		log.info("create project called ownerId={}, name={}", ownerId, request.name());
		try {
			User owner = userRepository.findById(ownerId)
					.orElseThrow(() -> {
						log.warn("create project owner not found ownerId={}", ownerId);
						return new ResourceNotFoundException("User not found");
					});

			Project project = new Project();
			project.setName(request.name().trim());
			project.setDescription(blankToNull(request.description()));
			project.setOwner(owner);

			projectRepository.save(project);

			Map<String, Object> data = new HashMap<>();
			data.put("projectResponse", toResponse(project));

			log.info("create project success projectId={}", project.getId());

			return GlobalApiResponse.success(ownerId.toString(), data);

		} catch (Exception e) {
			log.error("create project failed ownerId={}", ownerId, e);
			throw e;
		}
	}

	@Transactional
	public GlobalApiResponse update(UUID projectId, UUID ownerId, ProjectRequest request) {
		log.info("update project called projectId={}, ownerId={}", projectId, ownerId);
		try {
			Project project = projectRepository.findByIdAndOwner_Id(projectId, ownerId)
					.orElseThrow(() -> {
						log.warn("update project not found projectId={}, ownerId={}", projectId, ownerId);
						return new ResourceNotFoundException("Project not found");
					});

			project.setName(request.name().trim());
			project.setDescription(blankToNull(request.description()));

			Map<String, Object> data = new HashMap<>();
			data.put("projectResponse", toResponse(project));

			log.info("update project success projectId={}", projectId);

			return GlobalApiResponse.success(ownerId.toString(), data);

		} catch (Exception e) {
			log.error("update project failed projectId={}, ownerId={}", projectId, ownerId, e);
			throw e;
		}
	}

	@Transactional
	public GlobalApiResponse delete(UUID projectId, UUID ownerId) {
		log.info("delete project called projectId={}, ownerId={}", projectId, ownerId);
		try {
			Project project = projectRepository.findByIdAndOwner_Id(projectId, ownerId)
					.orElseThrow(() -> {
						log.warn("delete project not found projectId={}, ownerId={}", projectId, ownerId);
						return new ResourceNotFoundException("Project not found");
					});

			taskRepository.deleteAllByProject_Id(project.getId());
			taskRepository.flush();
			projectRepository.delete(project);

			log.info("delete project success projectId={}", projectId);

			return GlobalApiResponse.success(ownerId.toString(), null);

		} catch (Exception e) {
			log.error("delete project failed projectId={}, ownerId={}", projectId, ownerId, e);
			throw e;
		}
	}

	private static Map<String, Object> paginationMap(Page<?> page, int pageOneBased, int limit) {
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("page", pageOneBased);
		m.put("limit", limit);
		m.put("total", page.getTotalElements());
		m.put("total_pages", page.getTotalPages());
		return m;
	}

	private static String blankToNull(String s) {
		if (s == null || s.isBlank()) return null;
		return s;
	}

	private static ProjectResponse toResponse(Project p) {
		return new ProjectResponse(
				p.getId(),
				p.getName(),
				p.getDescription(),
				p.getOwner().getId(),
				p.getCreatedAt(),
				new ArrayList<>());
	}

	private static TaskResponse toTaskResponse(Task t) {
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