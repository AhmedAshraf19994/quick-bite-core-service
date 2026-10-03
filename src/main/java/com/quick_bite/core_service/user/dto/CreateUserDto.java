package com.quick_bite.core_service.user.dto;

import com.quick_bite.core_service.common.validation.ValueOfEnum;
import com.quick_bite.core_service.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserDto(

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Name is required")
        String name,

         @NotBlank(message = "Phone is required")
         @Pattern(
                 regexp = "^\\+?[0-9]{10,15}$",
                 message = "Phone number must be valid"
         )
         String phone,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$",
                message = "Password is not strong enough. It must contain at least 8 characters, one uppercase letter, one lowercase letter, one number."
        )
        String password,

        @NotBlank(message = "Role is required")
        @ValueOfEnum(
                enumClass = UserRole.class,
                message = "Invalid role"
        )
        String role
) {
}
