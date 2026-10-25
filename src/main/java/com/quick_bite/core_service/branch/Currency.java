package com.quick_bite.core_service.branch;

import com.quick_bite.core_service.common.exception.InvalidEnumTypeException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum Currency {
    EGP("egp"),
    SAR("sar");

    private final String value;

    public static Currency setCurrency(String value) {
         return Stream.of(values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidEnumTypeException("country code"));
    }
}
