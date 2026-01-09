// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.service;

import com.pilotquiz.quizservice.dto.QuizDTO;
import com.pilotquiz.quizservice.entity.Question;
import com.pilotquiz.quizservice.entity.Quiz;
import com.pilotquiz.quizservice.exception.ResourceNotFoundException;
import com.pilotquiz.quizservice.repository.QuestionRepository;
import com.pilotquiz.quizservice.repository.QuizRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuizService quizService;

    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = Quiz.builder()
                .id(1L)
                .title("Private Pilot Practice")
                .description("Practice quiz for PPL")
                .ratingType(Question.RatingType.PRIVATE)
                .timeLimitMinutes(30)
                .quizQuestions(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should return all quizzes paginated")
    void getAllQuizzes_Success() {
        // Given
        Page<Quiz> page = new PageImpl<>(Collections.singletonList(testQuiz));
        when(quizRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<QuizDTO> result = quizService.getAllQuizzes(PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Private Pilot Practice");
    }

    @Test
    @DisplayName("Should get quiz by ID")
    void getQuizById_Success() {
        // Given
        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(testQuiz));

        // When
        QuizDTO result = quizService.getQuizById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Private Pilot Practice");
    }

    @Test
    @DisplayName("Should throw exception when quiz not found")
    void getQuizById_NotFound_ThrowsException() {
        // Given
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> quizService.getQuizById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create new quiz")
    void createQuiz_Success() {
        // Given
        QuizDTO dto = QuizDTO.builder()
                .title("New Quiz")
                .ratingType(Question.RatingType.PRIVATE)
                .timeLimitMinutes(20)
                .build();

        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        // When
        QuizDTO result = quizService.createQuiz(dto);

        // Then
        assertThat(result).isNotNull();
        verify(quizRepository).save(any(Quiz.class));
    }

    @Test
    @DisplayName("Should delete quiz")
    void deleteQuiz_Success() {
        // Given
        when(quizRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(quizRepository).deleteById(anyLong());

        // When
        quizService.deleteQuiz(1L);

        // Then
        verify(quizRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent quiz")
    void deleteQuiz_NotFound_ThrowsException() {
        // Given
        when(quizRepository.existsById(anyLong())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> quizService.deleteQuiz(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
