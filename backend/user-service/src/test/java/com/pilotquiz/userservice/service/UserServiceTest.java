// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.service;

import com.pilotquiz.userservice.dto.UserProfileDTO;
import com.pilotquiz.userservice.entity.PilotProfile;
import com.pilotquiz.userservice.entity.User;
import com.pilotquiz.userservice.exception.ResourceNotFoundException;
import com.pilotquiz.userservice.repository.PilotProfileRepository;
import com.pilotquiz.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PilotProfileRepository pilotProfileRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private PilotProfile testProfile;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.ROLE_USER)
                .build();

        testProfile = PilotProfile.builder()
                .id(1L)
                .user(testUser)
                .currentRating(PilotProfile.Rating.PRIVATE)
                .targetRating(PilotProfile.Rating.INSTRUMENT)
                .flightHours(100)
                .build();
    }

    @Test
    @DisplayName("Should get user profile by ID")
    void getUserById_Success() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pilotProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(testProfile));

        // When
        UserProfileDTO profile = userService.getUserById(1L);

        // Then
        assertThat(profile).isNotNull();
        assertThat(profile.getEmail()).isEqualTo("test@example.com");
        assertThat(profile.getFirstName()).isEqualTo("John");
        assertThat(profile.getCurrentRating()).isEqualTo(PilotProfile.Rating.PRIVATE);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getUserById_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should update user profile")
    void updateProfile_Success() {
        // Given
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .currentRating(PilotProfile.Rating.INSTRUMENT)
                .targetRating(PilotProfile.Rating.COMMERCIAL)
                .flightHours(200)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pilotProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(testProfile));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(pilotProfileRepository.save(any(PilotProfile.class))).thenReturn(testProfile);
        doNothing().when(kafkaProducerService).sendProfileUpdatedEvent(anyLong());

        // When
        UserProfileDTO result = userService.updateProfile(1L, updateDTO);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
        verify(pilotProfileRepository).save(any(PilotProfile.class));
        verify(kafkaProducerService).sendProfileUpdatedEvent(anyLong());
    }
}
