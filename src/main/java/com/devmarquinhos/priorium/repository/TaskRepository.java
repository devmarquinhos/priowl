package com.devmarquinhos.priorium.repository;

import com.devmarquinhos.priorium.model.Task;
import com.devmarquinhos.priorium.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCategoryId(Long categoryId);

    List<Task> findByUserId(Long userId);

    List<Task> findByParentTaskId(Long parentTaskId);

    List<Task> findAllByParentTask_IdIn(List<Long> parentTaskIds);

    long countByUserIdAndStatus(Long userId, TaskStatus status);

    long countByUserIdAndStatusNot(Long userId, TaskStatus status);

    long countByParentTask_IdAndStatus(Long parentTaskId, TaskStatus status);

    long countByParentTask_IdAndStatusNot(Long parentTaskId, TaskStatus status);

    @Query("""
        SELECT t FROM Task t WHERE t.user.id = :userId
          AND (t.status = :status OR :status IS NULL)
          AND (t.importance = :importance OR :importance IS NULL)
          AND (:title IS NULL OR LOWER(t.title) LIKE :title)
    """)
    List<Task> filterUserTasks(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("importance") Integer importance,
            @Param("title") String title
    );
}
