// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for category operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private Long parentId;

    private List<CategoryDTO> children;
}
