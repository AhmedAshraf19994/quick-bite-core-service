package com.quick_bite.core_service.restaurant.dto;

public record UpdateRestaurantDto(
        String name,
        String primaryCountry,
        String logoUrl
) {
}
