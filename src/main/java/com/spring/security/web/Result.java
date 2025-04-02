package com.spring.security.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Result<T> {

    private final int code;
    private final String message;
    private final List<String> error;
    private final T data;

    private Result(int code, String message, Object error, T data) {
        this.code = code;
        this.message = message;
        this.error = Collections.singletonList(error.toString());
        this.data = data;
    }

    public static <T> Result<T> success(int code, String message, T data) {
        return new Result<>(code, message, List.of(), data);
    }

    public static <T> Result<T> failure(int code, String message, String error) {
        return new Result<>(code, message, Collections.singletonList(error), null);
    }

    public static <T> Result<T> failure(int code, String message, List<String> errors) {
        return new Result<>(code, message, errors, null);
    }

    public int getCode() {
        return code;
    }

    public List<String> getError() {
        return error;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
