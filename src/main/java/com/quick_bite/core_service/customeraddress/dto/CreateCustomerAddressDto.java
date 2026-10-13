package com.quick_bite.core_service.customeraddress.dto;

import com.quick_bite.core_service.common.validation.ValueOfEnum;
import com.quick_bite.core_service.customeraddress.AddressType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCustomerAddressDto(

        @NotBlank(message = "Label is required")
        String label,

        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "Building is required")
        String building,

        @NotBlank(message = "Apartment number is required")
        String apartmentNumber,

        @NotNull
        @ValueOfEnum( enumClass = AddressType.class,
                message = "Invalid address type. Allowed values are: HOME, WORK, OTHER"
        )
        String type,

        @NotNull
        @DecimalMax("180.0")
        @DecimalMin("-180.0")
        BigDecimal longitude,

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        BigDecimal latitude
        
) {
}
