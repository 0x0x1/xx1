package com.spring.security.web.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.security.service.AppUserService;
import com.spring.security.domain.AppUser;
import com.spring.security.repository.AuthorityRepository;
import com.spring.security.service.JwtTokenService;
import com.spring.security.web.Result;
import com.spring.security.web.payload.LoginRequestDto;
import com.spring.security.web.payload.LoginResponseDto;
import com.spring.security.web.payload.RegisterRequestDto;
import com.spring.security.web.payload.RegisterResponseDto;
import com.spring.security.web.config.MessageConfig;
import com.spring.security.web.utility.mapper.Mapper;

@RestController
@RequestMapping(path = AuthControllerDefinition.REST_AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController implements AuthControllerDefinition {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final AppUserService appUserService;
    private final AuthorityRepository authorityRepository;
    private final Mapper<AppUser, RegisterResponseDto> mapper;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AppUserService appUserService, AuthorityRepository authorityRepository, Mapper<AppUser, RegisterResponseDto> mapper, JwtTokenService jwtTokenService, AuthenticationManager authenticationManager) {
        this.appUserService = appUserService;
        this.authorityRepository = authorityRepository;
        this.mapper = mapper;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @PostMapping(value = REST_REGISTER_PATH)
    public ResponseEntity<Result<RegisterResponseDto>> register(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        LOGGER.debug("signup request: {}", registerRequestDto);

        boolean userExists = appUserService.existsByEmail(registerRequestDto.email());

        if (userExists) {
            return ResponseEntity.badRequest()
                    .body(Result.failure(HttpStatus.BAD_REQUEST.value(), MessageConfig.SIGN_UP_FAILED, MessageConfig.DUPLICATES_NOT_ALLOWED));
        } else {
            var appUser = createAppUser(registerRequestDto);
            AppUser signedUpUser = signUpUser(appUser);

            var registerResponseDto = mapper.toDto(signedUpUser);

            return ResponseEntity.ok(Result.success(HttpStatus.OK.value(), MessageConfig.SIGN_UP_SUCCESS, registerResponseDto));
        }
    }

    @Override
    @PostMapping(value = REST_LOGIN_PATH)
    public ResponseEntity<Result<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
        LOGGER.debug("login request: {}", loginRequestDto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var generatedToken = jwtTokenService.generateToken(authentication);

        return ResponseEntity.ok(Result.success(HttpStatus.OK.value(), MessageConfig.LOGIN_SUCCESS, generatedToken));
    }

    @GetMapping(value = "/test")
    public String test() {
        return "You are successfully accessing protected data. Currently authenticated username: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private AppUser createAppUser(RegisterRequestDto registerRequestDto) {
        return new AppUser.Builder()
                .setUsername(registerRequestDto.username())
                .setEmail(registerRequestDto.email())
                .setPassword(registerRequestDto.password())
                // defined roles
                .setAuthorities(List.of(authorityRepository.findByAuthorityName("ADMIN")))
                .build();
    }

    private AppUser signUpUser(AppUser appUser) {
        return appUserService.save(appUser);
    }
}