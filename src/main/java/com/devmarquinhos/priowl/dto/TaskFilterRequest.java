package com.devmarquinhos.priowl.dto;

import com.devmarquinhos.priowl.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskFilterRequest(
        TaskStatus status,
        Integer importance,
        String title,
        LocalDateTime deadlineBefore
) {
}
