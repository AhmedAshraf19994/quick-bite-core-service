package com.quick_bite.core_service.customeraddress;

import jakarta.persistence.AttributeConverter;

public class AddressTypeConverter implements AttributeConverter<AddressType, String> {
    @Override
    public String convertToDatabaseColumn(AddressType addressType) {
        return addressType == null ? null :addressType.name().toLowerCase();
    }

    @Override
    public AddressType convertToEntityAttribute(String value) {
        return value == null ? null : AddressType.valueOf(value.toUpperCase());
    }
}
