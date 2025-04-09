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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
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
import com.spring.security.web.payload.RegisterRequestDto;
import com.spring.security.web.payload.RegisterResponseDto;
import com.spring.security.web.utility.MessageUtil;
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
    private final MessageUtil messageUtil;

    public AuthController(AppUserService appUserService, AuthorityRepository authorityRepository,
                          Mapper<AppUser, RegisterResponseDto> mapper, JwtTokenService jwtTokenService,
                          AuthenticationManager authenticationManager, MessageUtil messageUtil) {
        this.appUserService = appUserService;
        this.authorityRepository = authorityRepository;
        this.mapper = mapper;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.messageUtil = messageUtil;
    }

    @Override
    @PostMapping(value = REST_REGISTER_PATH)
    public ResponseEntity<Result<?>> register(@RequestBody @Valid RegisterRequestDto requestDto, HttpServletRequest request) {
        LOGGER.debug("signup request: {}", requestDto);
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
                    .error(errorMessages)
                    .build();

            return ResponseEntity.status(BAD_REQUEST).body(result);
          }

        var appUser = createAppUser(requestDto);
        AppUser RegisteredUser = registerUser(appUser);
        String localizedMessage = messageUtil.getMessage(SIGN_UP_SUCCESS, locale);

        var registerResponseDto = mapper.toDto(RegisteredUser);

        var result = Result.buildWith()
                .code(CREATED)
                .message(localizedMessage)
                .data(registerResponseDto)
                .build();

        return ResponseEntity.status(CREATED).body(result);
    }

    @Override
    @PostMapping(value = REST_LOGIN_PATH)
    public ResponseEntity<Result<?>> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        LOGGER.debug("login request: {}", loginRequestDto);
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

    @GetMapping(value = REST_ADMIN_PATH)
    public String admin() {
        final var loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        final var role = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return "Protected Data only for admins. Logged in user: " + loggedInUser + ", role: " + role;
    }

    @GetMapping(value = REST_USER_PATH)
    public String user() {
        final var loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        final var role = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return "Protected Data for admins and users. Logged in user: " + loggedInUser + ", role: " + role;
    }

    @GetMapping(value = REST_PUBLIC_RESOURCE_PATH)
    public String test() {
        final var loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        final var role = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return "Protected Data for everyone " + loggedInUser + ", role: " + role;
    }

    private AppUser createAppUser(RegisterRequestDto registerRequestDto) {
        return new AppUser.Builder()
                .setUsername(registerRequestDto.username())
                .setEmail(registerRequestDto.email())
                .setPassword(registerRequestDto.password())
                .setAuthorities(List.of(authorityRepository.findByAuthorityName(registerRequestDto.authorityName())))
                .build();
    }

    private AppUser registerUser(AppUser appUser) {
        return appUserService.save(appUser);
    }
}