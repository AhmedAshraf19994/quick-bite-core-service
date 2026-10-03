package com.quick_bite.core_service.user.exceptions;

import com.quick_bite.core_service.common.exception.AppException;

public class UserExistsException extends AppException {
    public UserExistsException() {
        super("User already exists with the same email");
    }
}
