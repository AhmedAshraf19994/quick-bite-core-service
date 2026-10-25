package com.quick_bite.core_service.branch;

import com.quick_bite.core_service.common.exception.InvalidEnumTypeException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CountryCode {
    EGY("egy"),
    KSA("ksa");

    private String value;

    public static CountryCode setCode (String value) {
        return Stream.of(values())
            .filter(v -> v.value.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new InvalidEnumTypeException("country code"));
    }
}
