package com.quick_bite.core_service.branch;

import jakarta.persistence.AttributeConverter;

public class CurrencyConverter implements AttributeConverter<Currency, String> {


    @Override
    public String convertToDatabaseColumn(Currency attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Currency convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Currency.setCurrency(dbData);
    }
}
