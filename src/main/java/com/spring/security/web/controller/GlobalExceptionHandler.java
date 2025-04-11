package com.spring.security.web.controller;

import static com.spring.security.web.utility.ApplicationConstants.BAD_REQUEST;
import static com.spring.security.web.utility.ApplicationConstants.INTERNAL_SERVER_ERROR;
import static com.spring.security.web.utility.ApplicationConstants.SIGN_UP_FAILED;
import static com.spring.security.web.utility.ApplicationConstants.VALIDATION_FAILED;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.spring.security.web.utility.MessageUtil;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    public GlobalExceptionHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        //rethrow authentication exception so that spring security handles UsernameNotFound and BadCredentials
        if (ex instanceof AuthenticationException) {
            throw (AuthenticationException) ex;
        }

        Locale locale = request.getLocale();
        String localizedMessage = messageUtil.getMessage(SIGN_UP_FAILED, locale);
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        var result = Result.buildWith()
                .code(INTERNAL_SERVER_ERROR)
                .message(localizedMessage)
                .errors(errors)
                .build();

        //var result = Result.failure(INTERNAL_SERVER_ERROR, messageUtil.getMessage(SIGN_UP_FAILED, locale), e.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        Locale locale = request.getLocale();
        String localizedMessage = messageUtil.getMessage(VALIDATION_FAILED, locale);

        var result = Result.buildWith()
                .code(BAD_REQUEST)
                .message(localizedMessage)
                .errors(errors)
                .build();
        //var result = Result.failure(BAD_REQUEST, messageUtil.getMessage(VALIDATION_FAILED, locale), errors);
        return ResponseEntity.status(BAD_REQUEST).body(result);
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