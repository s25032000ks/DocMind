package com.ai.docMind.dto;

import com.ai.docMind.entity.Role;

public record UserDto(Long userId,
                      String userName,
                      Role role,
                      String email) {
}
