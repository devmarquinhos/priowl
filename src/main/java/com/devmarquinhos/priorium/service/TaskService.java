package com.devmarquinhos.priorium.service;

import com.devmarquinhos.priorium.dto.TaskRequest;
import com.devmarquinhos.priorium.dto.TaskResponse;
import com.devmarquinhos.priorium.model.Category;
import com.devmarquinhos.priorium.model.Task;
import com.devmarquinhos.priorium.model.User;
import com.devmarquinhos.priorium.repository.CategoryRepository;
import com.devmarquinhos.priorium.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    public TaskResponse createTask(TaskRequest request, User loggedUser) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setImportance(request.importance());
        task.setDeadline(request.deadline());
        task.setUser(loggedUser);

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

            if (!category.getUser().getId().equals(loggedUser.getId())) {
                throw new RuntimeException("Você não tem permissão para usar esta categoria.");
            }
            task.setCategory(category);
        }

        if (request.parentTaskId() != null) {
            Task parentTask = taskRepository.findById(request.parentTaskId())
                    .orElseThrow(() -> new RuntimeException("Tarefa pai não encontrada."));

            if (!parentTask.getUser().getId().equals(loggedUser.getId())) {
                throw new RuntimeException("Você não tem permissão para vincular a esta tarefa.");
            }
            task.setParentTask(parentTask);
        }

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    public List<TaskResponse> listUserTasks(User loggedUser) {
        return taskRepository.findByUserId(loggedUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse updateTask(Long id, TaskRequest request, User loggedUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        if (!task.getUser().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para alterar esta tarefa.");
        }

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setImportance(request.importance());
        task.setDeadline(request.deadline());

        if (request.status() != null) {
            try {
                task.setStatus(com.devmarquinhos.priorium.model.TaskStatus.valueOf(request.status().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Status inválido. Use PENDING, IN_PROGRESS, COMPLETED ou CANCELLED.");
            }
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));
            if (!category.getUser().getId().equals(loggedUser.getId())) {
                throw new RuntimeException("Você não tem permissão para usar esta categoria.");
            }
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        if (request.parentTaskId() != null) {
            if (request.parentTaskId().equals(id)) {
                throw new RuntimeException("Uma tarefa não pode ser pai dela mesma.");
            }
            Task parentTask = taskRepository.findById(request.parentTaskId())
                    .orElseThrow(() -> new RuntimeException("Tarefa pai não encontrada."));
            if (!parentTask.getUser().getId().equals(loggedUser.getId())) {
                throw new RuntimeException("Você não tem permissão para vincular a esta tarefa.");
            }
            task.setParentTask(parentTask);
        } else {
            task.setParentTask(null);
        }

        return mapToResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id, User loggedUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        if (!task.getUser().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Você não tem permissão para eliminar esta tarefa.");
        }

        List<Task> subTasks = taskRepository.findByParentTaskId(id);
        for (Task subTask : subTasks) {
            subTask.setParentTask(null);
            taskRepository.save(subTask);
        }

        taskRepository.delete(task);
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getImportance(),
                task.getDeadline(),
                task.getCategory() != null ? task.getCategory().getId() : null,
                task.getParentTask() != null ? task.getParentTask().getId() : null
        );
    }
}