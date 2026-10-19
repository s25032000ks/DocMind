package com.ai.docMind.dto;

import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record CitationDTO(UUID documentId,
                          String fileName,
                          Integer chunkIndex,
                          Integer pageNumber,
                          String snippet,
                          Double similarityScore,
                          Map<String, Object> metadata
) { }
