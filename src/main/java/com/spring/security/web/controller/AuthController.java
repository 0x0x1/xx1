package com.spring.security.web.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.security.business.service.AppUserService;
import com.spring.security.domain.AppUser;
import com.spring.security.repository.AuthorityRepository;
import com.spring.security.web.Result;
import com.spring.security.web.payload.SignUpRequestDto;
import com.spring.security.web.payload.SignUpResponseDto;
import com.spring.security.web.config.MessagesConfig;
import com.spring.security.web.utility.mapper.Mapper;

@RestController
@RequestMapping(path = AuthControllerDefinition.REST_AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController implements AuthControllerDefinition {

    private final AppUserService appUserService;
    private final AuthorityRepository authorityRepository;
    private final Mapper<AppUser, SignUpResponseDto> mapper;

    @Autowired
    public AuthController(AppUserService appUserService, AuthorityRepository authorityRepository, Mapper<AppUser, SignUpResponseDto> mapper) {
        this.appUserService = appUserService;
        this.authorityRepository = authorityRepository;
        this.mapper = mapper;
    }

    @Override
    @PostMapping(value = REST_SIGN_UP_PATH)
    public ResponseEntity<Result<SignUpResponseDto>> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {

        boolean userExists = appUserService.existsByEmail(signUpRequestDto.email());

        if (userExists) {
            return ResponseEntity.badRequest().body(Result.failure(HttpStatus.BAD_REQUEST.value(),
                    MessagesConfig.SIGN_UP_FAILED, MessagesConfig.DUPLICATES_NOT_ALLOWED));
        } else {

            var appUser = createAppUser(signUpRequestDto);
            AppUser savedUser = saveAppUser(appUser);
            var signUpResponseDto = mapper.toDto(savedUser);

            return ResponseEntity.ok(Result.success(HttpStatus.OK.value(), MessagesConfig.SIGN_UP_SUCCESS, signUpResponseDto));
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

    private AppUser saveAppUser(AppUser appUser) {
        return appUserService.save(appUser);
    }
}