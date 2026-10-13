package com.quick_bite.core_service.customeraddress.dto;

import com.quick_bite.core_service.common.validation.ValueOfEnum;
import com.quick_bite.core_service.customeraddress.AddressType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateCustomerAddressDto(

        String label,

        String country,

        String city,

        String street,

        String building,

        String apartmentNumber,

        @ValueOfEnum( enumClass = AddressType.class,
                message = "Invalid address type. Allowed values are: HOME, WORK, public_place"
        )
        String type,

        @DecimalMax("180.0")
        @DecimalMin("-180.0")
        BigDecimal longitude,

        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        BigDecimal latitude

) {
}
