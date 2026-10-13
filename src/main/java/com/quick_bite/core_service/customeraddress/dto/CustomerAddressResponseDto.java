package com.quick_bite.core_service.customeraddress.dto;


import com.quick_bite.core_service.customeraddress.AddressType;

import java.math.BigDecimal;
import java.time.Instant;

public record CustomerAddressResponseDto(
            Long id,
            String label,
            String country,
            String city,
            String street,
            String building,
            String apartmentNumber,
            AddressType type,
            boolean isDefault,
            BigDecimal longitude,
            BigDecimal latitude,
            Instant createdAt,
            Instant updatedAt
    ) {
}
