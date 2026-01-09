// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for answer options.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {

    private Long id;

    @NotBlank(message = "Answer text is required")
    private String answerText;

    @NotNull(message = "isCorrect flag is required")
    private Boolean isCorrect;
}
