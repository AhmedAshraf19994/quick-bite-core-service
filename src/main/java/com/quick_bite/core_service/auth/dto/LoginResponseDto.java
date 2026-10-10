package com.quick_bite.core_service.auth.dto;

import com.quick_bite.core_service.user.dto.UserResponseDto;

public record LoginResponseDto (
        String accessToken,
        UserResponseDto user
) {
}
