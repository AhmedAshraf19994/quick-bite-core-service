package com.quick_bite.core_service.product.dto;

public record UpdateProductDto(
        String name,
        String description,
        String imageUrl
) {
}
