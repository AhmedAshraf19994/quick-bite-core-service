package com.quick_bite.core_service.branch.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalTime;

public record UpdateBranchDto(
        String label,
        String addressText,

        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        BigDecimal lat,
        
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        BigDecimal lng,
        
        @Positive(message = "Delivery radius must be positive")
        Short deliveryRadius,
        LocalTime opensAt,
        LocalTime closesAt,
        Boolean acceptOrders,
        String currency,
        Short commission

) {
}
