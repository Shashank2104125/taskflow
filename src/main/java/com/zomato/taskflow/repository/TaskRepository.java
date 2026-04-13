package com.zomato.taskflow.repository;

import com.zomato.taskflow.entity.Task;
import com.zomato.taskflow.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

	List<Task> findAllTasksByProject_IdOrderByCreatedAtDesc(UUID projectId);

	@Query("""
			SELECT t FROM Task t
			WHERE t.project.id = :projectId
			AND (:status IS NULL OR t.status = :status)
			AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
			ORDER BY t.createdAt DESC
			""")
	List<Task> findByProjectFiltered(
			@Param("projectId") UUID projectId,
			@Param("status") TaskStatus status,
			@Param("assigneeId") UUID assigneeId);

	@Query("""
			SELECT t FROM Task t
			WHERE t.project.id = :projectId
			AND (:status IS NULL OR t.status = :status)
			AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
			""")
	Page<Task> findByProjectFilteredPage(
			@Param("projectId") UUID projectId,
			@Param("status") TaskStatus status,
			@Param("assigneeId") UUID assigneeId,
			Pageable pageable);

	@Query("""
			SELECT t.status, COUNT(t)
			FROM Task t
			WHERE t.project.id = :projectId
			GROUP BY t.status
			""")
	List<Object[]> countGroupedByStatus(@Param("projectId") UUID projectId);

	@Query("""
			SELECT t.assignee.id, COUNT(t)
			FROM Task t
			WHERE t.project.id = :projectId
			GROUP BY t.assignee.id
			""")
	List<Object[]> countGroupedByAssignee(@Param("projectId") UUID projectId);

	Optional<Task> findByIdAndProject_Id(UUID id, UUID projectId);

	@Modifying(clearAutomatically = true)
	void deleteAllByProject_Id(UUID projectId);
}
