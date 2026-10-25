package com.quick_bite.core_service.common.exception;

public class InvalidEnumTypeException extends AppException{

    public InvalidEnumTypeException(String name) {
        super("Invalid " + name +  " type");
    }
}
