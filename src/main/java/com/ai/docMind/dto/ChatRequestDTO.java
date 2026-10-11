package com.ai.docMind.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChatRequestDTO(@NotBlank(message = "Question cannot be empty") String question,
                             UUID documentId,
                             Integer topK,
                             Double minSimilarity,
                             String conversationId) {
}
