package com.ai.docMind.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ChatResponseDTO(String answer,
                              String conversationId,
                              List<CitationDTO> citationList,
                              Long responseTimeMs) {
}
