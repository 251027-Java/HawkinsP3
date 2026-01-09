// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.service;

import com.pilotquiz.progressservice.dto.QuestionResponseDTO;
import com.pilotquiz.progressservice.dto.QuizAttemptDTO;
import com.pilotquiz.progressservice.dto.UserAnalyticsDTO;
import com.pilotquiz.progressservice.dto.UserProgressDTO;
import com.pilotquiz.progressservice.entity.QuizAttempt;
import com.pilotquiz.progressservice.entity.UserProgress;
import com.pilotquiz.progressservice.exception.ResourceNotFoundException;
import com.pilotquiz.progressservice.repository.QuizAttemptRepository;
import com.pilotquiz.progressservice.repository.UserProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    @InjectMocks
    private ProgressService progressService;

    private QuizAttempt testAttempt;
    private UserProgress testProgress;

    @BeforeEach
    void setUp() {
        testAttempt = QuizAttempt.builder()
                .id(1L)
                .userId(1L)
                .quizId(1L)
                .quizTitle("Private Pilot Practice")
                .score(8)
                .totalQuestions(10)
                .completedAt(LocalDateTime.now())
                .build();

        testProgress = UserProgress.builder()
                .id(1L)
                .userId(1L)
                .categoryId(1L)
                .categoryName("Regulations")
                .totalAttempts(10)
                .correctCount(7)
                .build();
    }

    @Test
    @DisplayName("Should submit quiz attempt and calculate score")
    void submitAttempt_Success() {
        // Given
        QuizAttemptDTO dto = QuizAttemptDTO.builder()
                .quizId(1L)
                .quizTitle("Test Quiz")
                .responses(Arrays.asList(
                        QuestionResponseDTO.builder().questionId(1L).isCorrect(true).categoryId(1L).build(),
                        QuestionResponseDTO.builder().questionId(2L).isCorrect(false).categoryId(1L).build(),
                        QuestionResponseDTO.builder().questionId(3L).isCorrect(true).categoryId(1L).build()))
                .timeSpentSeconds(300)
                .build();

        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(testAttempt);
        when(userProgressRepository.findByUserIdAndCategoryId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testProgress));
        when(userProgressRepository.save(any(UserProgress.class))).thenReturn(testProgress);

        // When
        QuizAttemptDTO result = progressService.submitAttempt(1L, dto);

        // Then
        assertThat(result).isNotNull();
        verify(quizAttemptRepository).save(any(QuizAttempt.class));
    }

    @Test
    @DisplayName("Should get user attempts paginated")
    void getUserAttempts_Success() {
        // Given
        Page<QuizAttempt> page = new PageImpl<>(Collections.singletonList(testAttempt));
        when(quizAttemptRepository.findByUserIdOrderByCompletedAtDesc(anyLong(), any(Pageable.class)))
                .thenReturn(page);

        // When
        Page<QuizAttemptDTO> result = progressService.getUserAttempts(1L, PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getQuizTitle()).isEqualTo("Private Pilot Practice");
    }

    @Test
    @DisplayName("Should get attempt by ID")
    void getAttemptById_Success() {
        // Given
        when(quizAttemptRepository.findById(anyLong())).thenReturn(Optional.of(testAttempt));

        // When
        QuizAttemptDTO result = progressService.getAttemptById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(8);
    }

    @Test
    @DisplayName("Should throw exception when attempt not found")
    void getAttemptById_NotFound_ThrowsException() {
        // Given
        when(quizAttemptRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> progressService.getAttemptById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should get user progress by category")
    void getUserProgress_Success() {
        // Given
        when(userProgressRepository.findByUserIdOrderByTotalAttemptsDesc(anyLong()))
                .thenReturn(Collections.singletonList(testProgress));

        // When
        List<UserProgressDTO> result = progressService.getUserProgress(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryName()).isEqualTo("Regulations");
    }

    @Test
    @DisplayName("Should get user weak areas")
    void getWeakAreas_Success() {
        // Given
        when(userProgressRepository.findWeakAreasByUserId(anyLong()))
                .thenReturn(Collections.singletonList(testProgress));

        // When
        List<UserProgressDTO> result = progressService.getWeakAreas(1L, 5);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should get user analytics")
    void getUserAnalytics_Success() {
        // Given
        when(quizAttemptRepository.countByUserId(anyLong())).thenReturn(5L);
        when(quizAttemptRepository.getAverageScoreByUserId(anyLong())).thenReturn(75.0);
        when(quizAttemptRepository.getTotalQuestionsAttempted(anyLong())).thenReturn(50L);
        when(userProgressRepository.findWeakAreasByUserId(anyLong()))
                .thenReturn(Collections.singletonList(testProgress));
        when(userProgressRepository.findStrongAreasByUserId(anyLong()))
                .thenReturn(Collections.singletonList(testProgress));
        when(quizAttemptRepository.findByUserIdOrderByCompletedAtDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(testAttempt)));

        // When
        UserAnalyticsDTO result = progressService.getUserAnalytics(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalQuizzesTaken()).isEqualTo(5L);
        assertThat(result.getOverallAccuracy()).isEqualTo(75.0);
        assertThat(result.getTotalQuestionsAttempted()).isEqualTo(50L);
    }
}
