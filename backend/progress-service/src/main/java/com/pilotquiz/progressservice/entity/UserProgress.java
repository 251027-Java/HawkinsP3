// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserProgress entity for tracking per-category progress.
 */
@Entity
@Table(name = "user_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "category_id" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "total_attempts", nullable = false)
    @Builder.Default
    private Integer totalAttempts = 0;

    @Column(name = "correct_count", nullable = false)
    @Builder.Default
    private Integer correctCount = 0;

    @Column(name = "last_attempt")
    private LocalDateTime lastAttempt;

    /**
     * Calculate accuracy percentage.
     */
    public double getAccuracyPercentage() {
        if (totalAttempts == 0)
            return 0;
        return (double) correctCount / totalAttempts * 100;
    }

    /**
     * Increment stats based on question result.
     */
    public void recordAttempt(boolean isCorrect) {
        totalAttempts++;
        if (isCorrect) {
            correctCount++;
        }
        lastAttempt = LocalDateTime.now();
    }
}
