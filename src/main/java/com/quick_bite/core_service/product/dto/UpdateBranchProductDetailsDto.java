package com.quick_bite.core_service.product.dto;

public record UpdateBranchProductDetailsDto(
        int price,
        int stock,
        boolean isAvailable
) {
}
