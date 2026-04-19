package com.url.linkly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.url.linkly.dtos.LoginRequest;
import com.url.linkly.dtos.RegisterRequest;
import com.url.linkly.exception.UserAlreadyExistsException;
import com.url.linkly.models.User;
import com.url.linkly.security.jwt.JwtAuthenticationResponse;
import com.url.linkly.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    // ───── /api/auth/public/login ─────

    @Test
    @WithMockUser
    void login_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("vedanti");
        loginRequest.setPassword("password123");

        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("mock-jwt-token");
        when(userService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/auth/public/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    // ───── /api/auth/public/register ─────

    @Test
    @WithMockUser
    void register_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("vedanti");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("vedanti@gmail.com");

        when(userService.registerUser(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/api/auth/public/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    @WithMockUser
    void register_Fails_WhenUserAlreadyExists() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("vedanti");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("vedanti@gmail.com");

        when(userService.registerUser(any(User.class)))
                .thenThrow(new UserAlreadyExistsException("Email already exists."));

        mockMvc.perform(post("/api/auth/public/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists."));
    }
}