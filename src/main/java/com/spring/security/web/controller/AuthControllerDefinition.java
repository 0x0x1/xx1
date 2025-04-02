package com.spring.security.web.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.spring.security.web.Result;
import com.spring.security.web.payload.SignUpRequest;
import com.spring.security.web.payload.SignUpResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
* OpenAPI Documentation
* */
public interface AuthControllerDefinition {

    String REST_AUTH_PATH = "/api/auth";
    String REST_SIGN_UP_PATH = "/signup";

    @Operation(description = "Allows user to sign up.", method = "POST")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User has been created"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    ResponseEntity<Result<SignUpResponse>> signup(@RequestBody @Valid SignUpRequest signUpRequest);
}
