package com.devmarquinhos.priowl.repository;

import com.devmarquinhos.priowl.model.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {
    @Query("SELECT COUNT(td) FROM TaskDependency td WHERE td.blockedTask.id = :taskId AND td.blockingTask.status != 'COMPLETED'")
    long countPendingBlockingTasks(@Param("taskId") Long taskId);

    boolean existsByBlockingTaskIdAndBlockedTaskId(Long blockingTaskId, Long blockedTaskId);
}
