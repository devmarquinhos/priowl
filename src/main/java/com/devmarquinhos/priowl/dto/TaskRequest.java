package com.devmarquinhos.priowl.dto;

import java.time.LocalDateTime;

public record TaskRequest(
        String title,
        String description,
        String status,
        Integer importance,
        LocalDateTime deadline,
        Long categoryId,
        Long parentTaskId
) {
}
