package com.devmarquinhos.priorium.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "task_dependency")
public class TaskDependency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocking_task_id", nullable = false)
    private Task blockingTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_task_id", nullable = false)
    private Task blockedTask;

}
