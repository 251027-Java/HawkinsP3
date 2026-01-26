// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.service;

import com.pilotquiz.progressservice.dto.QuizAttemptDTO;
import com.pilotquiz.progressservice.dto.QuestionResponseDTO;
import com.pilotquiz.progressservice.dto.UserProgressDTO;
import com.pilotquiz.progressservice.dto.UserAnalyticsDTO;
import com.pilotquiz.progressservice.entity.QuizAttempt;
import com.pilotquiz.progressservice.entity.QuestionResponse;
import com.pilotquiz.progressservice.entity.UserProgress;
import com.pilotquiz.progressservice.exception.ResourceNotFoundException;
import com.pilotquiz.progressservice.repository.QuizAttemptRepository;
import com.pilotquiz.progressservice.repository.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for progress tracking and analytics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final UserProgressRepository userProgressRepository;

    /**
     * Submit a quiz attempt and update progress.
     */
    @Transactional
    public QuizAttemptDTO submitAttempt(Long userId, QuizAttemptDTO dto) {
        log.info("Submitting quiz attempt for user {} quiz {}", userId, dto.getQuizId());

        // Calculate score
        int score = 0;
        for (QuestionResponseDTO resp : dto.getResponses()) {
            if (Boolean.TRUE.equals(resp.getIsCorrect())) {
                score++;
            }
        }

        // Create attempt
        QuizAttempt attempt = QuizAttempt.builder()
                .userId(userId)
                .quizId(dto.getQuizId())
                .quizTitle(dto.getQuizTitle())
                .score(score)
                .totalQuestions(dto.getResponses().size())
                .timeSpentSeconds(dto.getTimeSpentSeconds())
                .build();

        // Add responses
        for (QuestionResponseDTO respDTO : dto.getResponses()) {
            QuestionResponse response = QuestionResponse.builder()
                    .questionId(respDTO.getQuestionId())
                    .selectedAnswerId(respDTO.getSelectedAnswerId())
                    .isCorrect(respDTO.getIsCorrect())
                    .categoryId(respDTO.getCategoryId())
                    .build();
            attempt.addResponse(response);

            // Update category progress
            if (respDTO.getCategoryId() != null) {
                updateCategoryProgress(userId, respDTO.getCategoryId(), respDTO.getIsCorrect());
            }
        }

        attempt = quizAttemptRepository.save(attempt);
        log.info("Quiz attempt saved with ID: {}, score: {}/{}", attempt.getId(), score, dto.getResponses().size());

        return toAttemptDTO(attempt);
    }

    /**
     * Get user's quiz attempts.
     */
    public Page<QuizAttemptDTO> getUserAttempts(Long userId, Pageable pageable) {
        return quizAttemptRepository.findByUserIdOrderByCompletedAtDesc(userId, pageable)
                .map(this::toAttemptDTO);
    }

    /**
     * Get attempt by ID.
     */
    public QuizAttemptDTO getAttemptById(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found: " + attemptId));
        return toAttemptDTO(attempt);
    }

    /**
     * Get user's progress by category.
     */
    public List<UserProgressDTO> getUserProgress(Long userId) {
        return userProgressRepository.findByUserIdOrderByTotalAttemptsDesc(userId).stream()
                .map(this::toProgressDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user's weak areas.
     */
    public List<UserProgressDTO> getWeakAreas(Long userId, int limit) {
        return userProgressRepository.findWeakAreasByUserId(userId).stream()
                .limit(limit)
                .map(this::toProgressDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get comprehensive user analytics.
     */
    public UserAnalyticsDTO getUserAnalytics(Long userId) {
        log.debug("Generating analytics for user: {}", userId);

        long totalQuizzes = quizAttemptRepository.countByUserId(userId);
        Double avgScore = quizAttemptRepository.getAverageScoreByUserId(userId);
        Long totalQuestions = quizAttemptRepository.getTotalQuestionsAttempted(userId);

        List<UserProgressDTO> weakAreas = getWeakAreas(userId, 5);
        List<UserProgressDTO> strongAreas = userProgressRepository.findStrongAreasByUserId(userId).stream()
                .limit(5)
                .map(this::toProgressDTO)
                .collect(Collectors.toList());

        Page<QuizAttemptDTO> recentAttempts = getUserAttempts(userId, PageRequest.of(0, 5));

        return UserAnalyticsDTO.builder()
                .userId(userId)
                .totalQuizzesTaken(totalQuizzes)
                .totalQuestionsAttempted(totalQuestions != null ? totalQuestions : 0L)
                .overallAccuracy(avgScore != null ? avgScore : 0.0)
                .weakAreas(weakAreas)
                .strongAreas(strongAreas)
                .recentAttempts(recentAttempts.getContent())
                .build();
    }

    /**
     * Update category progress for a user.
     */
    private void updateCategoryProgress(Long userId, Long categoryId, Boolean isCorrect) {
        UserProgress progress = userProgressRepository.findByUserIdAndCategoryId(userId, categoryId)
                .orElseGet(() -> UserProgress.builder()
                        .userId(userId)
                        .categoryId(categoryId)
                        .totalAttempts(0)
                        .correctCount(0)
                        .build());

        progress.recordAttempt(Boolean.TRUE.equals(isCorrect));
        userProgressRepository.save(progress);
    }

    private QuizAttemptDTO toAttemptDTO(QuizAttempt attempt) {
        return QuizAttemptDTO.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuizId())
                .quizTitle(attempt.getQuizTitle())
                .score(attempt.getScore())
                .totalQuestions(attempt.getTotalQuestions())
                .scorePercentage(attempt.getScorePercentage())
                .timeSpentSeconds(attempt.getTimeSpentSeconds())
                .completedAt(attempt.getCompletedAt().toString())
                .build();
    }

    private UserProgressDTO toProgressDTO(UserProgress progress) {
        return UserProgressDTO.builder()
                .categoryId(progress.getCategoryId())
                .categoryName(progress.getCategoryName())
                .totalAttempts(progress.getTotalAttempts())
                .correctCount(progress.getCorrectCount())
                .accuracyPercentage(progress.getAccuracyPercentage())
                .lastAttempt(progress.getLastAttempt() != null ? progress.getLastAttempt().toString() : null)
                .build();
    }
}
