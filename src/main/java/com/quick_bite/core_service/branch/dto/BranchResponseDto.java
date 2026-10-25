package com.quick_bite.core_service.branch.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BranchResponseDto(
        Long id,
        Long restaurantId,
        String countryCode,
        String label,
        String addressText,
        Boolean isActive,
        BigDecimal lat,
        BigDecimal lng,
        Short deliveryRadius,
        LocalTime closesAt,
        Boolean acceptOrders,
        String currency,
        Short commission,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
