// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.controller;

import com.pilotquiz.quizservice.dto.QuizDTO;
import com.pilotquiz.quizservice.entity.Question;
import com.pilotquiz.quizservice.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for quiz operations.
 */
@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Quiz management")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "Get all quizzes with optional rating filter")
    public ResponseEntity<Page<QuizDTO>> getAllQuizzes(
            @RequestParam(required = false) Question.RatingType ratingType,
            Pageable pageable) {

        Page<QuizDTO> quizzes;
        if (ratingType != null) {
            quizzes = quizService.getQuizzesByRatingType(ratingType, pageable);
        } else {
            quizzes = quizService.getAllQuizzes(pageable);
        }
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID with all questions")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        QuizDTO quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping
    @Operation(summary = "Create a new quiz (Admin only)")
    public ResponseEntity<QuizDTO> createQuiz(
            @Valid @RequestBody QuizDTO request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        QuizDTO quiz = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(quiz);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a quiz (Admin only)")
    public ResponseEntity<QuizDTO> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizDTO request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        QuizDTO quiz = quizService.updateQuiz(id, request);
        return ResponseEntity.ok(quiz);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a quiz (Admin only)")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
