package com.quick_bite.core_service.common.exception;

public class ForbiddenActionException extends AppException{

    public ForbiddenActionException() {
        super("Forbidden action");
    }
}
