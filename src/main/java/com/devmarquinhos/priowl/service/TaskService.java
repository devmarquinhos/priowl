package com.devmarquinhos.priowl.service;

import com.devmarquinhos.priowl.dto.DashboardResponse;
import com.devmarquinhos.priowl.dto.TaskFilterRequest;
import com.devmarquinhos.priowl.dto.TaskRequest;
import com.devmarquinhos.priowl.dto.TaskResponse;
import com.devmarquinhos.priowl.model.*;
import com.devmarquinhos.priowl.repository.CategoryRepository;
import com.devmarquinhos.priowl.repository.TaskDependencyRepository;
import com.devmarquinhos.priowl.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TaskDependencyRepository taskDependencyRepository;

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository, TaskDependencyRepository taskDependencyRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.taskDependencyRepository = taskDependencyRepository;
    }

    public TaskResponse createTask(TaskRequest request, User loggedUser) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(TaskStatus.valueOf(request.status()));
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

    private List<TaskResponse> enrichWithBranchProgress(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return emptyList();
        }

        List<Long> parentIds = tasks.stream().map(Task::getId).toList();
        List<Task> allSubTasks = taskRepository.findAllByParentTask_IdIn(parentIds);

        Map<Long, List<Task>> subTasksByParentId = allSubTasks.stream()
                .collect(Collectors.groupingBy(Task::getParentTaskId));

        return tasks.stream()
                .map(task -> {
                    List<Task> children = subTasksByParentId.getOrDefault(task.getId(), emptyList());
                    Double branchCalculated = this.calculateProgressFromList(children);
                    return this.mapToResponse(task, branchCalculated);
                })
                .toList();
    }

    public List<TaskResponse> listUserTasks(User loggedUser) {
        List<Task> userTasks = taskRepository.findByUserId(loggedUser.getId());

        return this.enrichWithBranchProgress(userTasks);
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
                var newStatus = com.devmarquinhos.priowl.model.TaskStatus.valueOf(request.status().toUpperCase());

                if (newStatus == com.devmarquinhos.priowl.model.TaskStatus.COMPLETED) {
                    long pendingDependencies = taskDependencyRepository.countPendingBlockingTasks(id);
                    if (pendingDependencies > 0) {
                        throw new RuntimeException("Não é possível concluir esta tarefa. Ela possui " + pendingDependencies + " tarefa(s) bloqueadora(s) pendente(s).");
                    }
                }

                task.setStatus(newStatus);
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

    public void addDependency(Long blockedTaskId, Long blockingTaskId, User loggedUser) {
        if (blockedTaskId.equals(blockingTaskId)) {
            throw new RuntimeException("Uma tarefa não pode depender dela mesma.");
        }

        Task blockedTask = taskRepository.findById(blockedTaskId)
                .orElseThrow(() -> new RuntimeException("Tarefa bloqueada não encontrada."));
        if (!blockedTask.getUser().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Permissão negada na tarefa bloqueada.");
        }

        Task blockingTask = taskRepository.findById(blockingTaskId)
                .orElseThrow(() -> new RuntimeException("Tarefa bloqueadora não encontrada."));
        if (!blockingTask.getUser().getId().equals(loggedUser.getId())) {
            throw new RuntimeException("Permissão negada na tarefa bloqueadora.");
        }

        if (taskDependencyRepository.existsByBlockingTaskIdAndBlockedTaskId(blockingTaskId, blockedTaskId)) {
            throw new RuntimeException("Esta dependência já existe.");
        }

        TaskDependency dependency = new TaskDependency();
        dependency.setBlockingTask(blockingTask);
        dependency.setBlockedTask(blockedTask);
        taskDependencyRepository.save(dependency);
    }

    private Double calculatePercentage(LongSupplier completedQuery, LongSupplier totalActiveQuery){
        long totalActive = totalActiveQuery.getAsLong();
        if (totalActive == 0) {
            return 0.0;
        }
        long completed = completedQuery.getAsLong();

        double percentage = ((double) completed / totalActive) * 100;

        return Math.floor(percentage);
    }

    public Double calculateBranchProgress(Long parentTaskId) {
        return this.calculatePercentage(
                () -> taskRepository.countByParentTask_IdAndStatus(parentTaskId, TaskStatus.COMPLETED),
                () -> taskRepository.countByParentTask_IdAndStatusNot(parentTaskId, TaskStatus.CANCELLED)
        );
    }

    private Double calculateProgressFromList(List<Task> subTasks) {
        if (subTasks == null || subTasks.isEmpty()) {
            return 0.0;
        }

        return this.calculatePercentage(
                () -> subTasks.stream().filter(Task::isEffectivelyCompleted).count(),
                () -> subTasks.stream().filter(Task::isEffectivelyActive).count()
        );
    }

    public List<TaskResponse> filterUserTasks(User loggedUser, TaskFilterRequest filter) {
        String titleSearch = filter.title() != null && !filter.title().isBlank() ? "%" + filter.title().toLowerCase() + "%" : null;

        List<Task> filtered = taskRepository.filterUserTasks(
                loggedUser.getId(),
                filter.status(),
                filter.importance(),
                titleSearch,
                filter.deadlineBefore()
        );
        return this.enrichWithBranchProgress(filtered);
    }

    public DashboardResponse getDashboardSummary(User loggedUser) {
        Long userId = loggedUser.getId();
        long completedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        long activeTasks = taskRepository.countByUserIdAndStatusNot(userId, TaskStatus.CANCELLED);
        long cancelledTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.CANCELLED);
        long inProgressTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
        long pendingTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);

        Double progress = this.calculatePercentage(
                () -> taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED),
                () -> taskRepository.countByUserIdAndStatusNot(userId, TaskStatus.CANCELLED)
        );

        return new DashboardResponse(
                progress,
                completedTasks,
                activeTasks,
                cancelledTasks,
                inProgressTasks,
                pendingTasks);
    }

    private TaskResponse mapToResponse(Task task){
        Double progress = this.calculateBranchProgress(task.getId());

        return this.mapToResponse(task, progress);
    }

    private TaskResponse mapToResponse(Task task, Double branchProgress) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getImportance(),
                task.getDeadline(),
                task.getCategory() != null ? task.getCategory().getId() : null,
                task.getParentTask() != null ? task.getParentTask().getId() : null,
                branchProgress
        );
    }
}