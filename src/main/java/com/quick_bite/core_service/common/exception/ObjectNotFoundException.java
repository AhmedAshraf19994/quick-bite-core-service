package com.quick_bite.core_service.common.exception;

public class ObjectNotFoundException extends AppException {

    public ObjectNotFoundException(String objectName) {
        super(objectName + " not found");
    }


}
