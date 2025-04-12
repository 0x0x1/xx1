package com.spring.security.web.controller;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestBody;

import com.spring.security.web.Result;
import com.spring.security.web.payload.LoginRequestDto;
import com.spring.security.web.payload.RegisterRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
* OpenAPI Documentation
* */
public interface AuthControllerDefinition {
    String BASE_PATH = "/api/auth";
    String REGISTRATION_PATH = "/public/register";
    String LOGIN_PATH = "/public/login";
    String ADMIN_PATH = "/private/admin";
    String USER_PATH = "/private/user";
    String PUBLIC_RESOURCE_PATH = "/public/resource";

    @Operation(description = "Allows user to register.", method = "POST")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User has been created"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    ResponseEntity<Result<?>> register(@NonNull HttpServletRequest request, @RequestBody @Valid RegisterRequestDto requestDto);

    @Operation(description = "Allows user to login.", method = "POST")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "login successful"),
            @ApiResponse(responseCode = "400", description = "User does not exist")
    })
    ResponseEntity<Result<?>> login(@NonNull HttpServletRequest request, @RequestBody LoginRequestDto loginRequestDto);

    String user();
    String admin();
    String visitor();
}