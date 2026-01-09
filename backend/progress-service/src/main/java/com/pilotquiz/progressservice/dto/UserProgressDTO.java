// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user progress in a category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDTO {

    private Long categoryId;
    private String categoryName;
    private Integer totalAttempts;
    private Integer correctCount;
    private Double accuracyPercentage;
    private String lastAttempt;
}
