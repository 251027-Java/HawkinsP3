// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.dto;

import com.pilotquiz.userservice.entity.PilotProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user profile responses and updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    // Pilot profile fields
    private PilotProfile.Rating currentRating;
    private PilotProfile.Rating targetRating;
    private Integer flightHours;
    private String preferredCategories;
}
