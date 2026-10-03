package com.quick_bite.core_service.common;

public record Response<T>(
        boolean flag,
        Integer code,
        String message,
        T data
) {
}
