package com.ai.docMind.dto;

import lombok.Builder;

@Builder
public record LoginRequestDTO(String userName, String password) {
}
