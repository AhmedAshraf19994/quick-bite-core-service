package com.quick_bite.core_service.rbac.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record CreateRestaurantMemberDto(

        @Positive(message = "number should be larget than 0")
        Long userId,

        @NotNull(message = "branch ids is required")
        Set<Long> branchIds,

        @NotEmpty(message = "role name is required")
        String roleName


) {
}
