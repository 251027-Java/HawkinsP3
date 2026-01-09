// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.service;

import com.pilotquiz.userservice.dto.UserProfileDTO;
import com.pilotquiz.userservice.entity.PilotProfile;
import com.pilotquiz.userservice.entity.User;
import com.pilotquiz.userservice.exception.ResourceNotFoundException;
import com.pilotquiz.userservice.repository.PilotProfileRepository;
import com.pilotquiz.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user profile operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PilotProfileRepository pilotProfileRepository;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Get user profile by user ID.
     */
    public UserProfileDTO getProfile(Long userId) {
        log.debug("Fetching profile for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        PilotProfile profile = pilotProfileRepository.findByUserId(userId)
                .orElse(null);

        return mapToDTO(user, profile);
    }

    /**
     * Update user profile.
     */
    @Transactional
    public UserProfileDTO updateProfile(Long userId, UserProfileDTO dto) {
        log.info("Updating profile for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Update user fields
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        userRepository.save(user);

        // Update pilot profile
        PilotProfile profile = pilotProfileRepository.findByUserId(userId)
                .orElseGet(() -> PilotProfile.builder().user(user).build());

        if (dto.getCurrentRating() != null) {
            profile.setCurrentRating(dto.getCurrentRating());
        }
        if (dto.getTargetRating() != null) {
            profile.setTargetRating(dto.getTargetRating());
        }
        if (dto.getFlightHours() != null) {
            profile.setFlightHours(dto.getFlightHours());
        }
        if (dto.getPreferredCategories() != null) {
            profile.setPreferredCategories(dto.getPreferredCategories());
        }

        pilotProfileRepository.save(profile);

        // Publish event
        kafkaProducerService.sendProfileUpdatedEvent(userId);

        log.info("Profile updated for user: {}", userId);

        return mapToDTO(user, profile);
    }

    /**
     * Get user by ID (admin operation).
     */
    public UserProfileDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        PilotProfile profile = pilotProfileRepository.findByUserId(userId).orElse(null);

        return mapToDTO(user, profile);
    }

    private UserProfileDTO mapToDTO(User user, PilotProfile profile) {
        UserProfileDTO.UserProfileDTOBuilder builder = UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name());

        if (profile != null) {
            builder.currentRating(profile.getCurrentRating())
                    .targetRating(profile.getTargetRating())
                    .flightHours(profile.getFlightHours())
                    .preferredCategories(profile.getPreferredCategories());
        }

        return builder.build();
    }
}
