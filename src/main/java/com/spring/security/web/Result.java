package com.spring.security.web;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {

    private final int code;
    private final String message;
    private final List<String> error = new ArrayList<>();
    private final String token;
    private final T data;

    private Result(int code, String message, List<String> error, String token, T data) {
        this.code = code;
        this.message = message;
        this.error.addAll(error);
        this.token = token;
        this.data = data;
    }

    public static <T> ResultBuilder<T> buildWith() {
        return new ResultBuilder<T>();
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
            this.code = code;
            return this;
        }
        public ResultBuilder<T> message(String message) {
            this.message = message;
            return this;
        }
        public ResultBuilder<T> error(List<String> error) {
            this.error.addAll(error);
            return this;
        }
        public ResultBuilder<T> data(T data) {
            this.data = data;
            return this;
        }
        public ResultBuilder<T> token(String token) {
            this.token = token;
            return this;
        }
        public Result<T> build() {
            return new Result<T>(this.code, this.message, this.error, this.token, this.data
            );
        }
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getError() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public T getData() {
        return data;
    }
}