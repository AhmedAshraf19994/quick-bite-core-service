package com.quick_bite.core_service.user.dto;

import com.quick_bite.core_service.user.UserRole;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        String phone,
        UserRole role
) {
}
