package com.quick_bite.core_service.product.dto;

import java.time.Instant;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        String imageUrl,
        String categoryName,
        String restaurantName,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
}
