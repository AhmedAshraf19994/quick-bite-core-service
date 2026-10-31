package com.quick_bite.core_service.product.dto;

public interface BranchProductResponseDto {

    Long getProductId();

    String getProductName();

    String getProductDescription();

    String getProductImageUrl();

    int getProductPrice();

    int getProductStock();

    boolean getIsAvailable();

}
