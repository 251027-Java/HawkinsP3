// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for submitting a quiz attempt.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDTO {

    private Long id;

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    private String quizTitle;

    @NotEmpty(message = "At least one response is required")
    private List<QuestionResponseDTO> responses;

    private Integer timeSpentSeconds;

    // Response fields
    private Integer score;
    private Integer totalQuestions;
    private Double scorePercentage;
    private String completedAt;
}
