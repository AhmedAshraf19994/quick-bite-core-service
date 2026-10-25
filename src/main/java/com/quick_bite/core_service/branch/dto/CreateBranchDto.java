package com.quick_bite.core_service.branch.dto;

import com.quick_bite.core_service.branch.CountryCode;
import com.quick_bite.core_service.branch.Currency;
import com.quick_bite.core_service.common.validation.ValueOfEnum;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalTime;

public record CreateBranchDto(

        @NotBlank(message = "Country code is required")
        @ValueOfEnum(enumClass = CountryCode.class)
        String countryCode,
        
        @NotBlank(message = "Label is required")
        String label,
        
        @NotBlank(message = "Address text is required")
        String addressText,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        BigDecimal lat,
        
        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        BigDecimal lng,
        
        @NotNull(message = "Delivery radius is required")
        @Positive(message = "Delivery radius must be positive")
        Short deliveryRadius,

        @NotNull(message = "Opens at time is required")
        LocalTime opensAt,
        
        @NotNull(message = "Closing time is required")
        LocalTime closesAt,
        
        @NotNull(message = "Accept orders status is required")
        Boolean acceptOrders,
        
        @NotBlank(message = "Currency is required")
        @ValueOfEnum(enumClass = Currency.class)
        String currency,
        
        @NotNull(message = "Commission is required")
        Short commission

) {
}
