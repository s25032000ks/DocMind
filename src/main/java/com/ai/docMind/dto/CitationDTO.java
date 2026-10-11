package com.ai.docMind.dto;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record CitationDTO(UUID documentId,
                          String fileName,
                          int chunkIndex,
                          int pageNumber,
                          String snippet,
                          double similarityScore,
                          Map<String, Object> metadata
) { }
