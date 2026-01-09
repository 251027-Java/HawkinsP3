// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk upload results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResultDTO {

    private int totalRows;
    private int successCount;
    private int errorCount;
    private List<String> errors;
}
