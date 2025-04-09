package com.spring.security.web.utility;

public final class ApplicationConstants {

    private ApplicationConstants() {
        throw new IllegalStateException("Utility class");
    }

    public final static String SIGN_UP_SUCCESS = "app.signup.success";
    public final static String SIGN_UP_FAILED = "app.signup.failure";
    public final static String LOGIN_SUCCESS = "app.login.success";
    public final static String DUPLICATES_NOT_ALLOWED = "app.duplicates.not.allowed";
    public final static String VALIDATION_FAILED = "app.validation.failed";

    public final static int CREATED = 201;
    public final static int BAD_REQUEST = 400;
    public final static int SUCCESS = 200;
    public final static int INTERNAL_SERVER_ERROR = 500;

}
