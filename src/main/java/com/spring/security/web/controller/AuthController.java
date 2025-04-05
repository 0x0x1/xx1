package com.spring.security.web.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.security.service.AppUserService;
import com.spring.security.domain.AppUser;
import com.spring.security.repository.AuthorityRepository;
import com.spring.security.service.TokenService;
import com.spring.security.web.Result;
import com.spring.security.web.payload.SignUpRequestDto;
import com.spring.security.web.payload.SignUpResponseDto;
import com.spring.security.web.config.MessageConfig;
import com.spring.security.web.utility.mapper.Mapper;

@RestController
@RequestMapping(path = AuthControllerDefinition.REST_AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController implements AuthControllerDefinition {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final AppUserService appUserService;
    private final AuthorityRepository authorityRepository;
    private final Mapper<AppUser, SignUpResponseDto> mapper;
    private final TokenService tokenService;

    @Autowired
    public AuthController(AppUserService appUserService, AuthorityRepository authorityRepository, Mapper<AppUser, SignUpResponseDto> mapper, TokenService tokenService) {
        this.appUserService = appUserService;
        this.authorityRepository = authorityRepository;
        this.mapper = mapper;
        this.tokenService = tokenService;
    }

    @Override
    @PostMapping(value = REST_SIGN_UP_PATH)
    public ResponseEntity<Result<SignUpResponseDto>> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        LOGGER.debug("signup request: {}", signUpRequestDto);

        boolean userExists = appUserService.existsByEmail(signUpRequestDto.email());

        if (userExists) {
            return ResponseEntity.badRequest()
                    .body(Result.failure(HttpStatus.BAD_REQUEST.value(), MessageConfig.SIGN_UP_FAILED, MessageConfig.DUPLICATES_NOT_ALLOWED));
        } else {
            var appUser = createAppUser(signUpRequestDto);
            AppUser signedUpUser = signUpUser(appUser);

            var signUpResponseDto = mapper.toDto(signedUpUser);
            var generatedToken = tokenService.generateToken(null);

            return ResponseEntity.ok(Result.success(HttpStatus.OK.value(), MessageConfig.SIGN_UP_SUCCESS, signUpResponseDto, generatedToken));
        }
    }

    private AppUser createAppUser(SignUpRequestDto signUpRequestDto) {
        return new AppUser.Builder()
                .setUsername(signUpRequestDto.username())
                .setEmail(signUpRequestDto.email())
                .setPassword(signUpRequestDto.password())
                // defined roles
                .setAuthorities(List.of(authorityRepository.findByAuthorityName("ADMIN")))
                .build();
    }

    private AppUser signUpUser(AppUser appUser) {
        return appUserService.save(appUser);
    }
}