package com.devmarquinhos.priorium.dto;

import com.devmarquinhos.priorium.model.TaskStatus;

public record TaskFilterRequest(
        TaskStatus status,
        Integer importance,
        String title
) {
}
