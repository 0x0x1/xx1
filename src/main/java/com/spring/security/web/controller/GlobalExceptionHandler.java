package com.spring.security.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.spring.security.web.Result;
import com.spring.security.web.payload.SignUpResponseDto;
import com.spring.security.web.config.MessageConfig;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<SignUpResponseDto>> handleUnexpectedException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(Result.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                MessageConfig.SIGN_UP_FAILED, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<SignUpResponseDto>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(Result.failure(HttpStatus.BAD_REQUEST.value(), MessageConfig.VALIDATION_FAILED, errors));
    }
}