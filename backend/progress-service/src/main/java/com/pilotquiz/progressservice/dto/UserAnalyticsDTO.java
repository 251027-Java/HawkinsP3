// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for user analytics dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnalyticsDTO {

    private Long userId;
    private Long totalQuizzesTaken;
    private Long totalQuestionsAttempted;
    private Double overallAccuracy;
    private List<UserProgressDTO> weakAreas;
    private List<UserProgressDTO> strongAreas;
    private List<QuizAttemptDTO> recentAttempts;
}
