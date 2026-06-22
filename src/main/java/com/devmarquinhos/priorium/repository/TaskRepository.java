package com.devmarquinhos.priorium.repository;

import com.devmarquinhos.priorium.model.Task;
import com.devmarquinhos.priorium.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
