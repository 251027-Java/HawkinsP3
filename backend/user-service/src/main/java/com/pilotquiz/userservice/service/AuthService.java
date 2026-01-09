// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.service;

import com.pilotquiz.userservice.dto.AuthResponse;
import com.pilotquiz.userservice.dto.LoginRequest;
import com.pilotquiz.userservice.dto.RegisterRequest;
import com.pilotquiz.userservice.entity.PilotProfile;
import com.pilotquiz.userservice.entity.User;
import com.pilotquiz.userservice.exception.EmailAlreadyExistsException;
import com.pilotquiz.userservice.exception.InvalidCredentialsException;
import com.pilotquiz.userservice.repository.PilotProfileRepository;
import com.pilotquiz.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations (register, login).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PilotProfileRepository pilotProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Register a new user.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(User.Role.ROLE_USER)
                .build();

        user = userRepository.save(user);

        // Create default pilot profile
        PilotProfile profile = PilotProfile.builder()
                .user(user)
                .currentRating(PilotProfile.Rating.STUDENT)
                .targetRating(PilotProfile.Rating.PRIVATE)
                .flightHours(0)
                .build();

        pilotProfileRepository.save(profile);

        // Publish event
        kafkaProducerService.sendUserRegisteredEvent(user.getId(), user.getEmail());

        // Generate token
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        log.info("User registered successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    /**
     * Authenticate user and return JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }
}
