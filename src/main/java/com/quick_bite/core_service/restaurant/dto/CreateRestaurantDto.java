package com.quick_bite.core_service.restaurant.dto;

import com.quick_bite.core_service.common.validation.ValueOfEnum;
import com.quick_bite.core_service.restaurant.RestaurantStatus;
import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantDto(
        @NotBlank(message = "Name is required")
        String name,
        String logoUrl,
        @NotBlank(message = "Primary country is required")
        String primaryCountry

) {
}
