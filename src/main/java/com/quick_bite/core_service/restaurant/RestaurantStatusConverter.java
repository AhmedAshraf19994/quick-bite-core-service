package com.quick_bite.core_service.restaurant;

import jakarta.persistence.AttributeConverter;

public class RestaurantStatusConverter implements AttributeConverter<RestaurantStatus, String> {

    @Override
    public String convertToDatabaseColumn(RestaurantStatus attribute) {
        return attribute == null ? null : attribute.getValue().toLowerCase();
    }

    @Override
    public RestaurantStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : RestaurantStatus.valueOf(dbData.toUpperCase());
    }
}
