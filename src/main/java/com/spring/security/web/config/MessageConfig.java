package com.spring.security.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-messages.properties")
public class MessageConfig {

    public static String SIGN_UP_SUCCESS;
    public static String SIGN_UP_FAILED;
    public static String USER_NOT_FOUND;
    public static String INVALID_CREDENTIALS;
    public static String DUPLICATES_NOT_ALLOWED;
    public static String UNKNOWN_ERROR;
    public static String VALIDATION_FAILED;
    public static String LOGIN_SUCCESS;
    public static String LOGIN_FAILED;

    @Value("${app.signup-success}")
    public void setSignUpSuccess(String value) {
        SIGN_UP_SUCCESS = value;
    }

    @Value("${app.signup.failed}")
    public void setSignUpFailed(String signUpFailed) {
        MessageConfig.SIGN_UP_FAILED = signUpFailed;
    }

    @Value("${app.login.failed}")
    public void loginFailed(String loginFailed) {
        MessageConfig.LOGIN_FAILED = loginFailed;
    }

    @Value("${app.login.success}")
    public void loginSuccess(String loginSuccess) {
        MessageConfig.LOGIN_SUCCESS = loginSuccess;
    }

    @Value("${app.validation.failed}")
    public void setValidationFailed(String validationFailed) {
        VALIDATION_FAILED = validationFailed;
    }


    @Value("${app.user.not.found}")
    public void setUserNotFound(String userNotFound) {
        USER_NOT_FOUND = userNotFound;
    }

    @Value("${app.invalid.credentials}")
    public void setInvalidCredentials(String invalidCredentials) {
        INVALID_CREDENTIALS = invalidCredentials;
    }

    @Value("${app.duplicates.not.allowed}")
    public void setDuplicatesNotAllowed(String duplicatesNotAllowed) {
        MessageConfig.DUPLICATES_NOT_ALLOWED = duplicatesNotAllowed;
    }

    @Value("${app.unknown.error}")
    public void setUnknownError(String unknownError) {
        UNKNOWN_ERROR = unknownError;
    }
}
