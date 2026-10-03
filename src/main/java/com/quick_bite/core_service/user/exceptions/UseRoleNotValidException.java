package com.quick_bite.core_service.user.exceptions;

import com.quick_bite.core_service.common.exception.AppException;

public class UseRoleNotValidException extends AppException {
    public UseRoleNotValidException() {
        super("Invalid user role");
    }
}
