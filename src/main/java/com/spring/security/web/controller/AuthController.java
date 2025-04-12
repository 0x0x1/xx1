package com.spring.security.web.controller;

import static com.spring.security.web.utility.ApplicationConstants.CREATED;
import static com.spring.security.web.utility.ApplicationConstants.LOGIN_SUCCESS;
import static com.spring.security.web.utility.ApplicationConstants.SIGN_UP_SUCCESS;
import static com.spring.security.web.utility.ApplicationConstants.SUCCESS;

import java.net.URI;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.spring.security.domain.User;
import com.spring.security.service.AppUserService;
import com.spring.security.repository.AuthorityRepository;
import com.spring.security.service.JwtTokenService;
import com.spring.security.web.Result;
import com.spring.security.web.exception.DuplicateUserException;
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
    public ResponseEntity<Result<?>> register(@NonNull  HttpServletRequest request, @RequestBody @Valid RegisterRequestDto requestDto) {
        boolean userExists = appUserService.existsByEmail(requestDto.email());

        if (userExists) {
            throw new DuplicateUserException("User already exists in the database");
        }

        var user = createUser(requestDto);
        var registeredUser = registerUser(user);
        var registerResponseDto = mapper.toDto(registeredUser);
        var localizedMessage = messageUtil.getMessage(SIGN_UP_SUCCESS, request.getLocale());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(REGISTRATION_PATH)
                .buildAndExpand(registeredUser.getId())
                .toUri();

        var result = Result.status(CREATED).message(localizedMessage).data(registerResponseDto).build();
        return ResponseEntity.created(location).body(result);
    }

    @Override
    @PostMapping(value = LOGIN_PATH)
    public ResponseEntity<Result<?>> login(@NonNull HttpServletRequest request, @RequestBody LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var generatedToken = jwtTokenService.generateToken(authentication);

        String localizedMessage = messageUtil.getMessage(LOGIN_SUCCESS, request.getLocale());

        var result = Result.status(SUCCESS).message(localizedMessage).token(generatedToken).build();
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