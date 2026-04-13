package com.zomato.taskflow.repository;

import com.zomato.taskflow.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

	@Query(value = """
			SELECT DISTINCT p FROM Project p
			WHERE p.owner.id = :userId
			OR EXISTS (
				SELECT 1 FROM Task t
				WHERE t.project = p
				AND (
					(t.assignee IS NOT NULL AND t.assignee.id = :userId)
					OR t.creator.id = :userId
				)
			)
			""",
			countQuery = """
			SELECT COUNT(DISTINCT p) FROM Project p
			WHERE p.owner.id = :userId
			OR EXISTS (
				SELECT 1 FROM Task t
				WHERE t.project = p
				AND (
					(t.assignee IS NOT NULL AND t.assignee.id = :userId)
					OR t.creator.id = :userId
				)
			)
			""")
	Page<Project> findAllOwnedOrWithTasksForUser(@Param("userId") UUID userId, Pageable pageable);

	Optional<Project> findByIdAndOwner_Id(UUID id, UUID ownerId);
}
