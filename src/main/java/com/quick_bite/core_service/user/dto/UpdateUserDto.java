package com.quick_bite.core_service.user.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateUserDto(
        String name,
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Invalid phone number"
        )
        String phone
) {
}
