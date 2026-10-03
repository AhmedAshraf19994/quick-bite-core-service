package com.quick_bite.core_service.common.exception;


import com.quick_bite.core_service.common.Response;
import com.quick_bite.core_service.user.exceptions.UserExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Response<?> handleObjectNotFoundException (ObjectNotFoundException exp) {
        return new Response<>(
                false,
                HttpStatus.NOT_FOUND.value(),
                exp.getMessage(),
                null
        );
    }

    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Response<?> handleUserExistsException (UserExistsException exp) {
        return new Response<>(
                false,
                HttpStatus.CONFLICT.value(),
                exp.getMessage(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Response<?> handleMethodArgumentNotValidException (MethodArgumentNotValidException exp) {
        Map<String, String> errors = exp.getBindingResult().getFieldErrors().stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                FieldError::getField,
                                FieldError::getDefaultMessage
                        )
                );
        return new Response<>(
                false,
                HttpStatus.BAD_REQUEST.value(),
                "Invalid input provided check data for details",
                errors
        );
    }




}
