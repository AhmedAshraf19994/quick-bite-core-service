package com.quick_bite.core_service.rbac.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateMemberBranchesDto(
        @NotEmpty
        Set<Long> branchIds
) {
}
