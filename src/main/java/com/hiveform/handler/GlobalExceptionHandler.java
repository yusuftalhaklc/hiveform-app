package com.hiveform.handler;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hiveform.dto.ApiResponse;
import com.hiveform.dto.RootResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleGeneralException(Exception ex, HttpServletRequest request) {
        return RootResponse.error(Arrays.asList(ex.getMessage()), "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return RootResponse.error(Arrays.asList(ex.getMessage()), "Resource not found",    HttpStatus.NOT_FOUND.value() , request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        return RootResponse.error(Arrays.asList(ex.getMessage()), "Unauthorized", HttpStatus.UNAUTHORIZED.value(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        return RootResponse.error(errors, "Validation failed", HttpStatus.BAD_REQUEST.value(), request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        
        return RootResponse.error(errors, "Validation failed", HttpStatus.BAD_REQUEST.value(), request.getRequestURI());
    }
}
