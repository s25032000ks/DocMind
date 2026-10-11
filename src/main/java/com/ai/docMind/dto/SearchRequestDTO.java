package com.ai.docMind.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record SearchRequestDTO(@NotBlank(message = "Query cannot be blank") String query,
                               UUID documentId,
                               Integer topK,
                               Double similaritySearch) {
}
