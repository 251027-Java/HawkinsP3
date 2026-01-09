// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.dto;

import com.pilotquiz.quizservice.entity.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating/updating questions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    private Long id;

    @NotBlank(message = "Question text is required")
    private String questionText;

    private String explanation;

    @NotNull(message = "Difficulty is required")
    private Question.Difficulty difficulty;

    @NotNull(message = "Rating type is required")
    private Question.RatingType ratingType;

    @NotEmpty(message = "At least one answer is required")
    private List<AnswerDTO> answers;

    private List<Long> categoryIds;
}
