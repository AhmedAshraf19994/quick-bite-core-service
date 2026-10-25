package com.quick_bite.core_service.branch.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateBranchStatusDto(
        @NotNull(message = "Branch status is required")
        boolean status
) {
}
