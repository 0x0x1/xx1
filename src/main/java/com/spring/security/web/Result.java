package com.spring.security.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.spring.security.web.utility.Status;

public class Result<T> {

    private final int code;
    private final String message;
    private final List<String> error;
    private final T data;

    public Result(int code, String message, Object error, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = Collections.singletonList(error.toString());
    }

    public static <T> Result<T> success(int code, String message, T data) {
        return new Result<>(code, message, List.of(), data);
    }

    public static <T> Result<T> failure(int code, String message, String errorMessage, T data) {
        var error = new ArrayList<>();
        error.add(errorMessage);
        return new Result<>(code, message, error, data);
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
