package com.quick_bite.core_service.customeraddress.exceptions;

import com.quick_bite.core_service.common.exception.AppException;

public class InvalidAddressTypeException extends AppException {

    public InvalidAddressTypeException() {
        super("Invalid address type");
    }
}
