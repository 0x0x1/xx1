package com.spring.security.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.security.service.AppUserService;
import com.spring.security.domain.AppUser;
import com.spring.security.domain.Authority;
import com.spring.security.repository.AuthorityRepository;
import com.spring.security.repository.UserRepository;
import com.spring.security.web.config.MessageConfig;
import com.spring.security.web.controller.AuthController;
import com.spring.security.web.payload.RegisterRequestDto;
import com.spring.security.web.payload.RegisterResponseDto;
import com.spring.security.web.utility.mapper.Mapper;

@Disabled
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc
@Import(MessageConfig.class)
public class AuthControllerTest {

    private static final String REST_SIGN_UP_URL = "/api/auth/public/register";

    @MockitoBean
    private AppUserService appUserService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthorityRepository authorityRepository;

    @MockitoBean
    private Mapper<AppUser, RegisterResponseDto> appUserMapper;

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objectMapper;

    RegisterRequestDto registerRequestDto;
    RegisterRequestDto registerRequestDtoInvalidEmail;
    RegisterResponseDto registerResponseDto;
    AppUser mockAppUser;
    Authority mockAuthority;

    @BeforeEach
    public void setUp() {
        //TODO create the mock objects more efficiently without populating the tests
        registerRequestDto = new RegisterRequestDto("testuser", "password123", "test@example.com");
        registerRequestDtoInvalidEmail = new RegisterRequestDto("testuser", "password123", "invalidEmail");
        registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setUsername("testuser");
        registerResponseDto.setPassword("password123");
        registerResponseDto.setEmail("test@example.com");

        mockAppUser = new AppUser.Builder()
                .setUsername("testuser")
                .setEmail("test@example.com")
                .setPassword("password123")
                .setAuthorities(List.of())
                .build();

        mockAuthority = new Authority();
        mockAuthority.setAuthorityName("ADMIN");
    }

    @Test
    void when_SignUp_With_InvalidEmail_ThenFail() throws JsonProcessingException {
        //Mock externals
        Mockito.when(appUserService.existsByEmail(anyString())).thenReturn(false);
        String jsonRequest = objectMapper.writeValueAsString(registerRequestDtoInvalidEmail);

        String expectedJson = "{\"code\":400,\"message\":\"Validation failed.\",\"error\":[\"[email: Invalid email]\"],\"data\":null}";

        mvc.perform(post(REST_SIGN_UP_URL)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .assertThat()
                .hasStatus(HttpStatus.BAD_REQUEST.value())
                .hasBodyTextEqualTo(expectedJson);
    }

    @Test
    void when_SignUp_With_DuplicateEmail_ThenFail() throws JsonProcessingException {
        //Mock externals
        Mockito.when(appUserService.existsByEmail(anyString())).thenReturn(true);
        String jsonRequest = objectMapper.writeValueAsString(registerRequestDto);

        String expectedJson = "{\"code\":400,\"message\":\"Sign up failed.\",\"error\":[\"[Duplicates not allowed.]\"],\"data\":null}";

        mvc.perform(post(REST_SIGN_UP_URL)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                            .assertThat()
                                .hasStatus(HttpStatus.BAD_REQUEST.value())
                                .hasBodyTextEqualTo(expectedJson);
    }

    @Test
    void when_SignUp_With_validData_ThenSuccess() throws JsonProcessingException {
        //Mock externals
        Mockito.when(appUserService.existsByEmail(anyString())).thenReturn(false);
        Mockito.when(appUserService.save(any())).thenReturn(mockAppUser);
        Mockito.when(appUserMapper.toDto(any())).thenReturn(registerResponseDto);
        Mockito.when(authorityRepository.findByAuthorityName(anyString())).thenReturn(mockAuthority);

        String jsonRequest = objectMapper.writeValueAsString(registerRequestDto);

        String expectedJson = "{\"code\":200,\"message\":\"User registration is successful.\",\"error\":[\"[]\"],\"data\":{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"authorities\":[]}}";

        mvc.perform(post(REST_SIGN_UP_URL)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .assertThat()
                .hasStatus(HttpStatus.OK.value())
                .hasBodyTextEqualTo(expectedJson);
    }

    @Test
    void when_SignUp_ThrowsUnexpectedException_Then500WithMessage() throws JsonProcessingException {
        //Mock externals
        Mockito.when(appUserService.existsByEmail(anyString()))
                .thenThrow(new RuntimeException("Something went wrong"));

        String jsonRequest = objectMapper.writeValueAsString(registerRequestDto);

        String expectedJson = "{\"code\":500,\"message\":\"Sign up failed.\",\"error\":[\"[Something went wrong]\"],\"data\":null}";

        mvc.perform(post(REST_SIGN_UP_URL)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .assertThat()
                .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .hasBodyTextEqualTo(expectedJson);
    }
}