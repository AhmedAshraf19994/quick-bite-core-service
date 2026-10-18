package com.quick_bite.core_service.restaurant.dto;

import com.quick_bite.core_service.restaurant.RestaurantStatus;

import java.time.Instant;

public record RestaurantResponseDto(
        Long id,
        String name,
        String primaryCountry,
        String logoUrl,
        RestaurantStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant statusUpdatedAt

) {
}
