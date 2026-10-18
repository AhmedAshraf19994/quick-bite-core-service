package com.quick_bite.core_service.security.refreshToken.exceptions;

import com.quick_bite.core_service.common.exception.AppException;

public class InvalidRefreshTokenException extends AppException {

    public InvalidRefreshTokenException() {
        super("Invalid RefreshToken");
    }
}
