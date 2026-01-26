// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pilot Profile entity storing aviation-specific user data.
 */
@Entity
@Table(name = "pilot_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PilotProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_rating")
    private Rating currentRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_rating")
    private Rating targetRating;

    @Column(name = "flight_hours")
    private Integer flightHours;

    @Column(name = "preferred_categories")
    private String preferredCategories; // Comma-separated category IDs

    public enum Rating {
        STUDENT,
        PRIVATE,
        INSTRUMENT,
        COMMERCIAL,
        ATP
    }
}
