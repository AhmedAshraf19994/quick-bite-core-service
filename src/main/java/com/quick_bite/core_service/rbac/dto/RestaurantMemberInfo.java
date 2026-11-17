package com.quick_bite.core_service.rbac.dto;

import java.util.Set;

public record RestaurantMemberInfo(
        Long restaurantId,
        String restaurantRole,
        Set<Long> branchIds
) {
}

