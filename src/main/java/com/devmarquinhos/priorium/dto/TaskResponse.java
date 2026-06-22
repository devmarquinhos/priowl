package com.devmarquinhos.priorium.dto;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        String status,
        Integer importance,
        LocalDateTime deadline,
        Long categoryId,
        Long parentTaskId,
        Double branchProgress
) {
}
