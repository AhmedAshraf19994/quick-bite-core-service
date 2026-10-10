package com.quick_bite.core_service.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record LoginUserDto (
        @NotEmpty(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        @NotEmpty(message = "Password is required")
        String password
){
}
