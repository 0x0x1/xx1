package com.spring.security.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.spring.security.web.Result;
import com.spring.security.web.payload.SignUpResponse;
import com.spring.security.web.utility.Message;
import com.spring.security.web.utility.Status;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<SignUpResponse>> handleUnexpectedException(Exception e) {
        return ResponseEntity.internalServerError().body(Result.failure(Status.InternalServerError,
                Message.SIGN_UP_FAILED, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<SignUpResponse>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(Result.failure(Status.BadRequest,
                        Message.VALIDATION_FAILED,
                        errors));
    }
}