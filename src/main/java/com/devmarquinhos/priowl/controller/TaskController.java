package com.devmarquinhos.priowl.controller;

import com.devmarquinhos.priowl.dto.DashboardResponse;
import com.devmarquinhos.priowl.dto.TaskFilterRequest;
import com.devmarquinhos.priowl.dto.TaskRequest;
import com.devmarquinhos.priowl.dto.TaskResponse;
import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal User loggedUser) {
        try {
            TaskResponse response = taskService.createTask(request, loggedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/dependencies/{blockingId}")
    public ResponseEntity<?> addDependency(
            @PathVariable Long id,
            @PathVariable Long blockingId,
            @AuthenticationPrincipal User loggedUser) {
        try {
            taskService.addDependency(id, blockingId, loggedUser);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("negada")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal User loggedUser) {
        try {
            TaskResponse response = taskService.updateTask(id, request, loggedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permissão")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listAll(
            @AuthenticationPrincipal User loggedUser) {
        List<TaskResponse> tasks = taskService.listUserTasks(loggedUser);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getSummary(
            @AuthenticationPrincipal User loggedUser){
        DashboardResponse dashboardResponse = taskService.getDashboardSummary(loggedUser);

        return ResponseEntity.ok(dashboardResponse);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TaskResponse>> filterTasks(
            @AuthenticationPrincipal User loggedUser,
            TaskFilterRequest filterParams
    ) {
        List<TaskResponse> responses = taskService.filterUserTasks(loggedUser, filterParams);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) {
        try {
            taskService.deleteTask(id, loggedUser);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permissão")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}