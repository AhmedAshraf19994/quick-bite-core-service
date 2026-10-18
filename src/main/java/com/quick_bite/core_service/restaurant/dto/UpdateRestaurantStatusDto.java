package com.quick_bite.core_service.restaurant.dto;

import com.quick_bite.core_service.common.validation.ValueOfEnum;
import com.quick_bite.core_service.restaurant.RestaurantStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRestaurantStatusDto(
        @NotNull
        @ValueOfEnum(enumClass = RestaurantStatus.class, message = "Invalid restaurant status")
        String status
) {
}
