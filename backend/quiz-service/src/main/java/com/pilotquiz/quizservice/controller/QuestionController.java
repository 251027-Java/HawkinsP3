// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.controller;

import com.pilotquiz.quizservice.dto.BulkUploadResultDTO;
import com.pilotquiz.quizservice.dto.QuestionDTO;
import com.pilotquiz.quizservice.entity.Question;
import com.pilotquiz.quizservice.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for question operations.
 */
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Questions", description = "Question management and bulk upload")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "Get all questions with optional filters")
    public ResponseEntity<Page<QuestionDTO>> getAllQuestions(
            @RequestParam(required = false) Question.RatingType ratingType,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable) {

        Page<QuestionDTO> questions;
        if (ratingType != null) {
            questions = questionService.getQuestionsByRatingType(ratingType, pageable);
        } else if (categoryId != null) {
            questions = questionService.getQuestionsByCategory(categoryId, pageable);
        } else {
            questions = questionService.getAllQuestions(pageable);
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        QuestionDTO question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @PostMapping
    @Operation(summary = "Create a new question (Admin only)")
    public ResponseEntity<QuestionDTO> createQuestion(
            @Valid @RequestBody QuestionDTO request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        QuestionDTO question = questionService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a question (Admin only)")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionDTO request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        QuestionDTO question = questionService.updateQuestion(id, request);
        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a question (Admin only)")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Bulk upload questions from CSV (Admin only)")
    public ResponseEntity<BulkUploadResultDTO> bulkUpload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BulkUploadResultDTO result = questionService.bulkUploadFromCsv(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/template")
    @Operation(summary = "Download CSV template for bulk upload")
    public ResponseEntity<String> getTemplate() {
        String template = questionService.getCsvTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "question_template.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(template);
    }
}
