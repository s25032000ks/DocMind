package com.ai.docMind.dto;

import com.ai.docMind.entity.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadataDTO {
    private UUID uuid;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private int totalPages;
    private int totalChunks;
    private DocumentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;
}