package com.spring.security.web.controller;

import java.net.URI;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.spring.security.web.Result;
import com.spring.security.web.payload.RegisterResponseDto;
import com.spring.security.web.config.MessageConfig;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<RegisterResponseDto>> handleUnexpectedException(Exception e) {
        //rethrow authentication exception so that spring security handles UsernameNotFound and BadCredentials
        if (e instanceof AuthenticationException) {
            throw (AuthenticationException) e;
        }
        return ResponseEntity.internalServerError()
                .body(Result.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                MessageConfig.SIGN_UP_FAILED, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<RegisterResponseDto>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest()
                .body(Result.failure(HttpStatus.BAD_REQUEST.value(), MessageConfig.VALIDATION_FAILED, errors));
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtException(JwtException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Invalid JWT Token");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}