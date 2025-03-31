package com.spring.security.web.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.security.business.service.AppUserService;
import com.spring.security.domain.AppUser;
import com.spring.security.web.utility.Message;
import com.spring.security.web.Result;
import com.spring.security.web.payload.SignUpRequest;
import com.spring.security.web.payload.UserDTO;
import com.spring.security.web.utility.Status;

@RestController
@RequestMapping(path = AuthControllerDefinition.REST_AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController implements AuthControllerDefinition {

    private final AppUserService appUserService;

    @Autowired
    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping(value = REST_SIGN_UP_PATH)
    public Result<?> signup(@RequestBody SignUpRequest signUpRequest) {

        int status = appUserService.existsByEmail(signUpRequest.email());

        try {
            switch (status) {
                case Status.Conflict -> {
                    return Result.failure(Status.Conflict,
                            Message.SIGN_UP_FAILED, Message.DUPLICATES_NOT_ALLOWED, Optional.empty());
                }
                case Status.Accepted -> {
                    var appUser = new AppUser();
                    appUser.setUsername(signUpRequest.username());
                    appUser.setPassword(signUpRequest.password());
                    appUser.setEmail(signUpRequest.email());

                    var savedUser = appUserService.save(appUser);

                    var userDTO = new UserDTO(savedUser.getUsername(),
                            savedUser.getPassword(),
                            savedUser.getEmail());

                    return Result.success(Status.OK, Message.SIGN_UP_SUCCESS, userDTO);
                }
                default -> {
                    return Result.failure(Status.BadRequest,
                            Message.SIGN_UP_FAILED, Message.UNKNOWN_ERROR, Optional.empty());
                }
            }
        } catch (Exception e) {
            return Result.failure(Status.InternalServerError,
                    Message.SIGN_UP_FAILED, e.getMessage(), Optional.empty());
        }
    }
}
