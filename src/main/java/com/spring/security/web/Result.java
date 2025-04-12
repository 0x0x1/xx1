package com.spring.security.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public class Result<T> {
    private final int code;
    private final String message;
    private final List<String> errors = new ArrayList<>();
    private final String token;
    private final T data;

    private Result(int code, String message, List<String> errors, String token, T data) {
        this.code = code;
        this.message = message;
        this.errors.addAll(errors);
        this.token = token;
        this.data = data;
    }

    public int getCode() { return this.code; }

    public String getMessage() { return this.message; }

    public List<String> getErrors() { return this.errors; }

    public String getToken() { return this.token; }

    public T getData() { return this.data; }

    public static <T> ResultBuilder<T> status(int code) {
        return new ResultBuilder<T>().code(code);
    }

    public static final class ResultBuilder<T> {
        private int code;
        private String message;
        private final List<String> error = new ArrayList<>();
        private String token;
        private T data;

        private ResultBuilder() {
        }

        public ResultBuilder<T> code(int code) {
            Assert.isTrue(code >= 100 && code <= 599, "Invalid HTTP status code: " + code);
            this.code = code;
            return this;
        }

        public ResultBuilder<T> message(String message) {
            Assert.notNull(message, "message cannot be null");
            this.message = message;
            return this;
        }

        public ResultBuilder<T> errors(List<String> errors) {
            Assert.notNull(errors, "errors cannot be null");
            this.error.addAll(errors);
            return this;
        }

        public ResultBuilder<T> data(T data) {
            Assert.notNull(data, "data cannot be null");
            this.data = data;
            return this;
        }

        public ResultBuilder<T> token(String token) {
            Assert.notNull(token, "token cannot be null");
            this.token = token;
            return this;
        }

        public Result<T> build() {
            return new Result<T>(this.code, this.message, this.error, this.token, this.data
            );
        }
    }
}