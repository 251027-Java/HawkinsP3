// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.controller;

import com.pilotquiz.progressservice.dto.QuizAttemptDTO;
import com.pilotquiz.progressservice.dto.UserAnalyticsDTO;
import com.pilotquiz.progressservice.dto.UserProgressDTO;
import com.pilotquiz.progressservice.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for progress tracking operations.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "Quiz attempts and progress tracking")
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/attempts")
    @Operation(summary = "Submit a quiz attempt")
    public ResponseEntity<QuizAttemptDTO> submitAttempt(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody QuizAttemptDTO request) {

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        QuizAttemptDTO result = progressService.submitAttempt(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/attempts")
    @Operation(summary = "Get current user's quiz attempts")
    public ResponseEntity<Page<QuizAttemptDTO>> getUserAttempts(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            Pageable pageable) {

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        Page<QuizAttemptDTO> attempts = progressService.getUserAttempts(userId, pageable);
        return ResponseEntity.ok(attempts);
    }

    @GetMapping("/attempts/{id}")
    @Operation(summary = "Get attempt by ID")
    public ResponseEntity<QuizAttemptDTO> getAttemptById(@PathVariable Long id) {
        QuizAttemptDTO attempt = progressService.getAttemptById(id);
        return ResponseEntity.ok(attempt);
    }

    @GetMapping("/progress")
    @Operation(summary = "Get current user's progress by category")
    public ResponseEntity<List<UserProgressDTO>> getUserProgress(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<UserProgressDTO> progress = progressService.getUserProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get comprehensive analytics for current user")
    public ResponseEntity<UserAnalyticsDTO> getUserAnalytics(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        UserAnalyticsDTO analytics = progressService.getUserAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/weak-areas")
    @Operation(summary = "Get user's weak areas for focused study")
    public ResponseEntity<List<UserProgressDTO>> getWeakAreas(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "5") int limit) {

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<UserProgressDTO> weakAreas = progressService.getWeakAreas(userId, limit);
        return ResponseEntity.ok(weakAreas);
    }
}
