// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.service;

import com.pilotquiz.userservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;
    private String secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Set up test secret key (min 256 bits for HS256)
        secretKey = Base64.getEncoder().encodeToString(
                "this-is-a-test-secret-key-that-is-long-enough-for-hs256".getBytes());
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L); // 24 hours

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_Success() {
        // When
        String token = jwtService.generateToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract email from token")
    void extractEmail_Success() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        String email = jwtService.extractEmail(token);

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void extractUserId_Success() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        Long userId = jwtService.extractUserId(token);

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should extract role from token")
    void extractRole_Success() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        String role = jwtService.extractRole(token);

        // Then
        assertThat(role).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("Should validate valid token")
    void validateToken_Valid_ReturnsTrue() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        boolean isValid = jwtService.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void validateToken_Invalid_ReturnsFalse() {
        // When
        boolean isValid = jwtService.validateToken("invalid.token.here");

        // Then
        assertThat(isValid).isFalse();
    }
}
