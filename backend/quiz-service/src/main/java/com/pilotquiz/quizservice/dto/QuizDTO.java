// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.dto;

import com.pilotquiz.quizservice.entity.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for quiz operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Rating type is required")
    private Question.RatingType ratingType;

    private Integer timeLimitMinutes;

    private List<Long> questionIds;

    // For response - includes full question data
    private List<QuestionDTO> questions;

    private Integer questionCount;
}
