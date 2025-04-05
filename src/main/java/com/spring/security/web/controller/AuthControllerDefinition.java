package com.spring.security.web.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.spring.security.web.Result;
import com.spring.security.web.payload.LoginRequestDto;
import com.spring.security.web.payload.LoginResponseDto;
import com.spring.security.web.payload.RegisterRequestDto;
import com.spring.security.web.payload.RegisterResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
* OpenAPI Documentation
* */
public interface AuthControllerDefinition {

    String REST_AUTH_PATH = "/api/auth";
    String REST_REGISTER_PATH = "/public/register";
    String REST_LOGIN_PATH = "/public/login";

    @Operation(description = "Allows user to register.", method = "POST")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User has been created"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    ResponseEntity<Result<RegisterResponseDto>> register(@RequestBody @Valid RegisterRequestDto registerRequestDto);

    @Operation(description = "Allows user to login.", method = "POST")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "login successful"),
            @ApiResponse(responseCode = "400", description = "User does not exist")
    })
    ResponseEntity<Result<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto);

}
