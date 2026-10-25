package com.quick_bite.core_service.branch;

import jakarta.persistence.AttributeConverter;

public class CountryCodeConverter implements AttributeConverter<CountryCode, String> {

    @Override
    public String convertToDatabaseColumn(CountryCode attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CountryCode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : CountryCode.setCode(dbData);
    }
}
