package com.spring.security.web.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.security.business.service.AppUserService;
import com.spring.security.domain.AppUser;
import com.spring.security.repository.AuthorityRepository;
import com.spring.security.web.utility.Message;
import com.spring.security.web.Result;
import com.spring.security.web.payload.SignUpRequest;
import com.spring.security.web.payload.SignUpResponse;
import com.spring.security.web.utility.Status;

@RestController
@RequestMapping(path = AuthControllerDefinition.REST_AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController implements AuthControllerDefinition {

    private final AppUserService appUserService;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthController(AppUserService appUserService, AuthorityRepository authorityRepository) {
        this.appUserService = appUserService;
        this.authorityRepository = authorityRepository;
    }

    @Override
    @PostMapping(value = REST_SIGN_UP_PATH)
    public ResponseEntity<Result<SignUpResponse>> signup(@RequestBody @Valid SignUpRequest signUpRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            var errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.badRequest().body(Result.failure(Status.BadRequest,
                    Message.SIGN_UP_FAILED, errorMessage));
        }

        int status = appUserService.existsByEmail(signUpRequest.email());

        try {
            switch (status) {
                case Status.Conflict -> {
                    return ResponseEntity.unprocessableEntity().body(Result.failure(Status.Conflict,
                            Message.SIGN_UP_FAILED, Message.DUPLICATES_NOT_ALLOWED));
                }
                case Status.Accepted -> {
                    var appUser = new AppUser.Builder()
                            .setUsername(signUpRequest.username())
                            .setEmail(signUpRequest.email())
                            .setPassword(signUpRequest.password())
                            // a registered user has hardcoded roles
                            .setAuthorities(List.of(authorityRepository.findByAuthorityName("ADMIN"), authorityRepository.findByAuthorityName("USER")))
                            .build();

                    var savedUser = appUserService.save(appUser);

                    var userDTO = new SignUpResponse(savedUser.getUsername(),
                            savedUser.getPassword(),
                            savedUser.getEmail());

                    return ResponseEntity.ok(Result.success(Status.OK, Message.SIGN_UP_SUCCESS, userDTO));
                }
                default -> {
                    return ResponseEntity.badRequest().body(Result.failure(Status.BadRequest,
                            Message.SIGN_UP_FAILED, Message.UNKNOWN_ERROR));
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Result.failure(Status.InternalServerError,
                    Message.SIGN_UP_FAILED, e.getMessage()));
        }
    }
}