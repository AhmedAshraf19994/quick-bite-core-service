package com.quick_bite.core_service.restaurant;

import com.quick_bite.core_service.restaurant.exceptions.InvalidRestaurantStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum RestaurantStatus {
    ACTIVE("active"),
    SUSPENDED("suspended"),
    DISABLED("disabled"),
    PENDING("pending");

    private final String value;

    public static RestaurantStatus setStatus(String statusAsString) {
        return Stream.of(values())
                .filter(status -> status.getValue().equalsIgnoreCase(statusAsString))
                .findFirst()
                .orElseThrow(InvalidRestaurantStatus::new);
    }
}
