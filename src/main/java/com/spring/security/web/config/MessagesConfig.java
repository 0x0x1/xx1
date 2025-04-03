package com.spring.security.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-messages.properties")
public class MessagesConfig {

    public static String SIGN_UP_SUCCESS;
    public static String SIGN_UP_FAILED;
    public static String USER_NOT_FOUND;
    public static String INVALID_CREDENTIALS;
    public static String DUPLICATES_NOT_ALLOWED;
    public static String UNKNOWN_ERROR;
    public static String VALIDATION_FAILED;

    @Value("${app.signup-success}")
    public void setSignUpSuccess(String value) {
        SIGN_UP_SUCCESS = value;
    }

    @Value("${app.signup.failed}")
    public void setSignUpFailed(String signUpFailed) {
        MessagesConfig.SIGN_UP_FAILED = signUpFailed;
    }

    @Value("${app.user.not.found}")
    public void setUserNotFound(String userNotFound) {
        USER_NOT_FOUND = userNotFound;
    }

    @Value("${app.invalid.credentials}")
    public static void setInvalidCredentials(String invalidCredentials) {
        INVALID_CREDENTIALS = invalidCredentials;
    }

    @Value("${app.duplicates.not.allowed}")
    public void setDuplicatesNotAllowed(String duplicatesNotAllowed) {
        MessagesConfig.DUPLICATES_NOT_ALLOWED = duplicatesNotAllowed;
    }

    @Value("${app.unknown.error}")
    public static void setUnknownError(String unknownError) {
        UNKNOWN_ERROR = unknownError;
    }

    @Value("${app.validation.failed}")
    public static void setValidationFailed(String validationFailed) {
        VALIDATION_FAILED = validationFailed;
    }
}
