package com.ai.docMind.dto;

import com.ai.docMind.entity.DocumentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DocumentMetadataDTO (UUID uuid,
        String fileName,
        String contentType,
        Long fileSize,
        int totalPages,
        int totalChunks,
        DocumentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String errorMessage) {
}
