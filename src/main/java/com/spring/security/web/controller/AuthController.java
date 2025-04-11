package com.spring.security.web.controller;

import static com.spring.security.web.utility.ApplicationConstants.BAD_REQUEST;
import static com.spring.security.web.utility.ApplicationConstants.CREATED;
import static com.spring.security.web.utility.ApplicationConstants.DUPLICATES_NOT_ALLOWED;
import static com.spring.security.web.utility.ApplicationConstants.LOGIN_SUCCESS;
import static com.spring.security.web.utility.ApplicationConstants.SIGN_UP_FAILED;
import static com.spring.security.web.utility.ApplicationConstants.SIGN_UP_SUCCESS;
import static com.spring.security.web.utility.ApplicationConstants.SUCCESS;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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

import com.spring.security.domain.User;
import com.spring.security.service.AppUserService;
import com.spring.security.repository.AuthorityRepository;
import com.spring.security.service.JwtTokenService;
import com.spring.security.web.Result;
import com.spring.security.web.payload.LoginRequestDto;
import com.spring.security.web.payload.RegisterRequestDto;
import com.spring.security.web.payload.RegisterResponseDto;
import com.spring.security.web.utility.MessageUtil;
import com.spring.security.web.utility.mapper.Mapper;

@RestController
@RequestMapping(path = AuthControllerDefinition.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController implements AuthControllerDefinition {
    private final AppUserService appUserService;
    private final AuthorityRepository authorityRepository;
    private final Mapper<User, RegisterResponseDto> mapper;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final MessageUtil messageUtil;

    public AuthController(AppUserService appUserService, AuthorityRepository authorityRepository,
                          Mapper<User, RegisterResponseDto> mapper, JwtTokenService jwtTokenService,
                          AuthenticationManager authenticationManager, MessageUtil messageUtil) {
        this.appUserService = appUserService;
        this.authorityRepository = authorityRepository;
        this.mapper = mapper;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.messageUtil = messageUtil;
    }

    @Override
    @PostMapping(value = REGISTRATION_PATH)
    public ResponseEntity<Result<?>> register(@RequestBody @Valid RegisterRequestDto requestDto, HttpServletRequest request) {
        //If your system is in en_US then replace request.getLocale() with Locale.GERMAN to test the result.
        Locale locale = Locale.GERMAN;

        boolean userExists = appUserService.existsByEmail(requestDto.email());

        if (userExists) {
            String localizedMessage = messageUtil.getMessage(SIGN_UP_FAILED, locale);
            String errorMessage = messageUtil.getMessage(DUPLICATES_NOT_ALLOWED, locale);
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add(errorMessage);

            var result = Result.buildWith()
                    .code(BAD_REQUEST)
                    .message(localizedMessage)
                    .errors(errorMessages)
                    .build();

            return ResponseEntity.status(BAD_REQUEST).body(result);
          }

        var user = createUser(requestDto);
        User registeredUser = registerUser(user);
        String localizedMessage = messageUtil.getMessage(SIGN_UP_SUCCESS, locale);

        var registerResponseDto = mapper.toDto(registeredUser);

        var result = Result.buildWith()
                .code(CREATED)
                .message(localizedMessage)
                .data(registerResponseDto)
                .build();

        return ResponseEntity.status(CREATED).body(result);
    }

    @Override
    @PostMapping(value = LOGIN_PATH)
    public ResponseEntity<Result<?>> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        Locale locale = Locale.GERMAN;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var generatedToken = jwtTokenService.generateToken(authentication);

        String localizedMessage = messageUtil.getMessage(LOGIN_SUCCESS, locale);

        var result = Result.buildWith()
                .code(SUCCESS)
                .message(localizedMessage)
                .token(generatedToken)
                .build();

        return ResponseEntity.status(SUCCESS).body(result);
    }

    @GetMapping(value = ADMIN_PATH)
    public String admin() {
        final var loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        final var role = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return "Protected Data only for admins. Logged in user: " + loggedInUser + ", role: " + role;
    }

    @GetMapping(value = USER_PATH)
    public String user() {
        final var loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        final var role = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return "Protected Data for admins and users. Logged in user: " + loggedInUser + ", role: " + role;
    }

    @GetMapping(value = PUBLIC_RESOURCE_PATH)
    public String visitor() {
        final var loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        final var role = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return "Protected Data for everyone " + loggedInUser + ", role: " + role;
    }

    private User createUser(RegisterRequestDto registerRequestDto) {
        return new User.Builder()
                .setUsername(registerRequestDto.username())
                .setEmail(registerRequestDto.email())
                .setPassword(registerRequestDto.password())
                .setAuthorities(List.of(authorityRepository.findByAuthorityName(registerRequestDto.authorityName())))
                .build();
    }

    private User registerUser(User user) {
        return appUserService.save(user);
    }
}