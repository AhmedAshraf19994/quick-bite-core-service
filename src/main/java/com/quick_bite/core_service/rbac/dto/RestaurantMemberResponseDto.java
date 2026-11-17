package com.quick_bite.core_service.rbac.dto;

import java.time.Instant;
import java.util.Set;

public record RestaurantMemberResponseDto(
        Long memberId,
        Long userId,
        String restaurantRole,
        Long restaurantId,
        Set<Long> branchIds

) {
}
