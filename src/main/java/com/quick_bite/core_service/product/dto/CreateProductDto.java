package com.quick_bite.core_service.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProductDto(

        @NotBlank(message = "name is required")
        String name,
        String description,
        String imageUrl,
        @NotBlank(message = "category name is required")
        String categoryName
) {
}
