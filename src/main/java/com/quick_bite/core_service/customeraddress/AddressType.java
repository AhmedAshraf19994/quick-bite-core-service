package com.quick_bite.core_service.customeraddress;

import com.quick_bite.core_service.customeraddress.exceptions.InvalidAddressTypeException;

import java.util.stream.Stream;

public enum AddressType {
    HOME("home"),
    WORK("work"),
    PUBLIC_PLACE("public_place");

    private final String value;

    AddressType(String value) {
        this.value = value;
    }

    public static AddressType setAddressType(String addressTypeAsString) {
        return Stream.of(values())
                .filter(addressType -> addressType.value.equalsIgnoreCase(addressTypeAsString))
                .findFirst()
                .orElseThrow(InvalidAddressTypeException::new);
    }

    public String getValue() {
        return value;
    }
}
