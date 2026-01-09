// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.service;

import com.pilotquiz.quizservice.dto.AnswerDTO;
import com.pilotquiz.quizservice.dto.QuestionDTO;
import com.pilotquiz.quizservice.entity.Answer;
import com.pilotquiz.quizservice.entity.Category;
import com.pilotquiz.quizservice.entity.Question;
import com.pilotquiz.quizservice.exception.ResourceNotFoundException;
import com.pilotquiz.quizservice.repository.CategoryRepository;
import com.pilotquiz.quizservice.repository.QuestionRepository;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private QuestionService questionService;

    private Question testQuestion;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Regulations")
                .build();

        testQuestion = Question.builder()
                .id(1L)
                .questionText("What is the minimum visibility for VFR?")
                .explanation("FAR 91.155 requires 3 SM")
                .difficulty(Question.Difficulty.MEDIUM)
                .ratingType(Question.RatingType.PRIVATE)
                .categories(new HashSet<>(Collections.singletonList(testCategory)))
                .answers(new ArrayList<>())
                .build();

        Answer answer = Answer.builder()
                .id(1L)
                .answerText("3 statute miles")
                .isCorrect(true)
                .question(testQuestion)
                .build();
        testQuestion.getAnswers().add(answer);
    }

    @Test
    @DisplayName("Should return all questions paginated")
    void getAllQuestions_Success() {
        // Given
        Page<Question> page = new PageImpl<>(Collections.singletonList(testQuestion));
        when(questionRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<QuestionDTO> result = questionService.getAllQuestions(PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getQuestionText()).contains("VFR");
    }

    @Test
    @DisplayName("Should get question by ID")
    void getQuestionById_Success() {
        // Given
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(testQuestion));

        // When
        QuestionDTO result = questionService.getQuestionById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuestionText()).contains("VFR");
        assertThat(result.getAnswers()).hasSize(1);
    }

    @Test
    @DisplayName("Should throw exception when question not found")
    void getQuestionById_NotFound_ThrowsException() {
        // Given
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> questionService.getQuestionById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create new question")
    void createQuestion_Success() {
        // Given
        QuestionDTO dto = QuestionDTO.builder()
                .questionText("Test question?")
                .difficulty(Question.Difficulty.EASY)
                .ratingType(Question.RatingType.PRIVATE)
                .answers(Collections.singletonList(
                        AnswerDTO.builder().answerText("Answer 1").isCorrect(true).build()))
                .categoryIds(Collections.singletonList(1L))
                .build();

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // When
        QuestionDTO result = questionService.createQuestion(dto);

        // Then
        assertThat(result).isNotNull();
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("Should delete question")
    void deleteQuestion_Success() {
        // Given
        when(questionRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(questionRepository).deleteById(anyLong());

        // When
        questionService.deleteQuestion(1L);

        // Then
        verify(questionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent question")
    void deleteQuestion_NotFound_ThrowsException() {
        // Given
        when(questionRepository.existsById(anyLong())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> questionService.deleteQuestion(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return CSV template")
    void getCsvTemplate_ReturnsContent() {
        // When
        String template = questionService.getCsvTemplate();

        // Then
        assertThat(template).contains("question_text");
        assertThat(template).contains("difficulty");
        assertThat(template).contains("rating_type");
    }
}
