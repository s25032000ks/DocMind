package com.ai.docMind.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApiResponse<T>(Boolean success,
                             String message,
                             LocalDateTime timestamp,
                             T data) {
}
