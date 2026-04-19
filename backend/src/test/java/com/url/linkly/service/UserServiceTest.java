package com.url.linkly.service;

import com.url.linkly.dtos.LoginRequest;
import com.url.linkly.exception.UserAlreadyExistsException;
import com.url.linkly.models.User;
import com.url.linkly.repository.UserRepository;
import com.url.linkly.security.jwt.JwtAuthenticationResponse;
import com.url.linkly.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("vedanti");
        user.setEmail("vedanti@gmail.com");
        user.setPassword("password123");
        user.setRole("ROLE_USER");
    }

    // ───── registerUser ─────

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registerUser_ThrowsException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ThrowsException_WhenUsernameAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any());
    }

    // ───── authenticateUser ─────

    @Test
    void authenticateUser_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("vedanti");
        loginRequest.setPassword("password123");

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails)).thenReturn("mock-jwt-token");

        JwtAuthenticationResponse response = userService.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
    }

    // ───── findByUsername ─────

    @Test
    void findByUsername_Success() {
        when(userRepository.findByUsername("vedanti")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("vedanti");

        assertNotNull(result);
        assertEquals("vedanti", result.getUsername());
    }

    @Test
    void findByUsername_ThrowsException_WhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername("unknown"));
    }
}