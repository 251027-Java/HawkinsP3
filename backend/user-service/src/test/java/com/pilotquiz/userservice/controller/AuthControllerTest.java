package com.pilotquiz.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilotquiz.userservice.config.SecurityConfig;
import com.pilotquiz.userservice.dto.AuthResponse;
import com.pilotquiz.userservice.dto.LoginRequest;
import com.pilotquiz.userservice.dto.RegisterRequest;
import com.pilotquiz.userservice.exception.EmailAlreadyExistsException;
import com.pilotquiz.userservice.exception.InvalidCredentialsException;
import com.pilotquiz.userservice.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("Should register user successfully")
        void register_Success() throws Exception {
                // Given
                RegisterRequest request = RegisterRequest.builder()
                                .email("test@example.com")
                                .password("password123")
                                .firstName("John")
                                .lastName("Doe")
                                .build();

                AuthResponse response = AuthResponse.builder()
                                .token("jwt-token")
                                .email("test@example.com")
                                .firstName("John")
                                .lastName("Doe")
                                .role("USER")
                                .build();

                when(authService.register(any(RegisterRequest.class))).thenReturn(response);

                // When/Then
                mockMvc.perform(post("/api/v1/auth/register")
                                .with(csrf()) // Security best practice in tests, though disabled in config
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.token").value("jwt-token"))
                                .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void register_EmailExists_Returns409() throws Exception {
                // Given
                RegisterRequest request = RegisterRequest.builder()
                                .email("existing@example.com")
                                .password("password123")
                                .firstName("John")
                                .lastName("Doe")
                                .build();

                when(authService.register(any(RegisterRequest.class)))
                                .thenThrow(new EmailAlreadyExistsException("Email already registered"));

                // When/Then
                mockMvc.perform(post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should login user successfully")
        void login_Success() throws Exception {
                // Given
                LoginRequest request = LoginRequest.builder()
                                .email("test@example.com")
                                .password("password123")
                                .build();

                AuthResponse response = AuthResponse.builder()
                                .token("jwt-token")
                                .email("test@example.com")
                                .build();

                when(authService.login(any(LoginRequest.class))).thenReturn(response);

                // When/Then
                mockMvc.perform(post("/api/v1/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("jwt-token"));
        }

        @Test
        @DisplayName("Should return 401 for invalid credentials")
        void login_InvalidCredentials_Returns401() throws Exception {
                // Given
                LoginRequest request = LoginRequest.builder()
                                .email("test@example.com")
                                .password("wrongpassword")
                                .build();

                when(authService.login(any(LoginRequest.class)))
                                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

                // When/Then
                mockMvc.perform(post("/api/v1/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 400 for invalid request body")
        void register_InvalidRequest_Returns400() throws Exception {
                // Given - missing required fields
                RegisterRequest request = RegisterRequest.builder()
                                .email("") // empty email
                                .password("pass")
                                .build();

                // When/Then
                mockMvc.perform(post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }
}
