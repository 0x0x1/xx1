package com.spring.security.web.exception;

public class DuplicateUserException extends RuntimeException {

    private final String message;

    public DuplicateUserException(String message) {
        super(message);
        this.message = message;
    }
}
