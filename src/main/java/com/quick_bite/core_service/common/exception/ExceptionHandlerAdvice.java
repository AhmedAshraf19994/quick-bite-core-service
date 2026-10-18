package com.quick_bite.core_service.common.exception;


import com.quick_bite.core_service.common.Response;
import com.quick_bite.core_service.user.exceptions.UserExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> handleUsernameNotFoundException (UsernameNotFoundException exp) {
        return new Response<>(
                false,
                HttpStatus.UNAUTHORIZED.value(),
                "Bad credentials",
                null
        );
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> handleInsufficientAuthenticationException (InsufficientAuthenticationException exp) {
        return new Response<>(
                false,
                HttpStatus.UNAUTHORIZED.value(),
                "Please sign in",
                null
        );
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> handleInvalidBearerTokenException (InvalidBearerTokenException exception) {
        return new Response<>(
                false,
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid token",
                null
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    Response<?> handleAccessDeniedException (AccessDeniedException exp) {
        return new Response<>(
                false,
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                null
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> handleBadCredentialsException (BadCredentialsException exp) {
        return new Response<>(
                false,
                HttpStatus.UNAUTHORIZED.value(),
                "Bad credentials",
                null
        );
    }

    // handle refresh token absence
    @ExceptionHandler({MissingRequestCookieException.class, HandlerMethodValidationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> handleCookieAbsenceException (Exception exp) {
        return new Response<>(
                false,
                HttpStatus.UNAUTHORIZED.value(),
                "Refresh token is missing or invalid",
                null
        );
    }

    // to catch unhandled exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Response<?> handleUnhandledException (Exception exp) {

        log.error("An unexpected error occurred: ", exp);
        return new Response<>(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                null
        );
    }




}
