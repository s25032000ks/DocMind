package com.ai.docMind.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SearchResultDTO(String query, int totalMatches, List<CitationDTO> matches) {
}
