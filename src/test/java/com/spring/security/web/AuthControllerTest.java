package com.spring.security.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.security.business.service.AppUserService;
import com.spring.security.domain.AppUser;
import com.spring.security.repository.AuthorityRepository;
import com.spring.security.repository.UserRepository;
import com.spring.security.web.config.MessagesConfig;
import com.spring.security.web.controller.AuthController;
import com.spring.security.web.payload.SignUpRequestDto;
import com.spring.security.web.payload.SignUpResponseDto;
import com.spring.security.web.utility.mapper.Mapper;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc
@Import(MessagesConfig.class)
public class AuthControllerTest {

    private static final String REST_SIGN_UP_URL = "/api/auth/signup";

    @MockitoBean
    private AppUserService appUserService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthorityRepository authorityRepository;

    @MockitoBean
    private Mapper<AppUser, SignUpResponseDto> appUserMapper;

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objectMapper;

    SignUpRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        requestDto = new SignUpRequestDto("testuser", "password123", "test@example.com");
    }

    @Test
    void when_SignUp_With_DuplicateEmail_ThenFail() throws JsonProcessingException {
        Mockito.when(appUserService.existsByEmail(Mockito.anyString())).thenReturn(true);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        String expectedJson = "{\"code\":400,\"message\":\"Sign up failed.\",\"error\":[\"[Duplicates not allowed.]\"],\"data\":null}";

        mvc.perform(post(REST_SIGN_UP_URL)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .assertThat()
                    .hasStatus(400)
                    .hasBodyTextEqualTo(expectedJson);
    }
}