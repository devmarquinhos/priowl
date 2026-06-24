package com.devmarquinhos.priorium.dto;

import com.devmarquinhos.priorium.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskFilterRequest(
        TaskStatus status,
        Integer importance,
        String title,
        LocalDateTime deadlineBefore
) {
}
