package com.quick_bite.core_service.product.dto;

public interface RestaurantProductResponseDto {

    Long getId();

    String getProductName();

    String getProductDescription();

    String getProductImageUrl();

    String getCategoryName();

    String getRestaurantName();
}
