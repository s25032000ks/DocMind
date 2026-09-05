package com.ai.docMind.dto;

import com.ai.docMind.entity.DocumentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DocumentResponseDTO(UUID uuid,
                                  String fileName,
                                  Long fileSize,
                                  DocumentStatus status,
                                  int chunkCreated,
                                  String message
) { }
