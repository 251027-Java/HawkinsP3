// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.service;

import com.pilotquiz.quizservice.dto.QuestionDTO;
import com.pilotquiz.quizservice.dto.QuizDTO;
import com.pilotquiz.quizservice.entity.Question;
import com.pilotquiz.quizservice.entity.Quiz;
import com.pilotquiz.quizservice.exception.ResourceNotFoundException;
import com.pilotquiz.quizservice.repository.QuestionRepository;
import com.pilotquiz.quizservice.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for quiz operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    /**
     * Get all quizzes with pagination.
     */
    public Page<QuizDTO> getAllQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable).map(this::toSummaryDTO);
    }

    /**
     * Get quizzes by rating type.
     */
    public Page<QuizDTO> getQuizzesByRatingType(Question.RatingType ratingType, Pageable pageable) {
        return quizRepository.findByRatingType(ratingType, pageable).map(this::toSummaryDTO);
    }

    /**
     * Get quiz by ID with full question details.
     */
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + id));
        return toFullDTO(quiz);
    }

    /**
     * Create a new quiz.
     */
    @Transactional
    public QuizDTO createQuiz(QuizDTO dto) {
        log.info("Creating quiz: {}", dto.getTitle());

        Quiz quiz = Quiz.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .ratingType(dto.getRatingType())
                .timeLimitMinutes(dto.getTimeLimitMinutes())
                .build();

        // Add questions if provided
        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            int order = 1;
            for (Long questionId : dto.getQuestionIds()) {
                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));
                quiz.addQuestion(question, order++);
            }
        }

        quiz = quizRepository.save(quiz);
        log.info("Quiz created with ID: {}", quiz.getId());

        return toFullDTO(quiz);
    }

    /**
     * Update an existing quiz.
     */
    @Transactional
    public QuizDTO updateQuiz(Long id, QuizDTO dto) {
        log.info("Updating quiz: {}", id);

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + id));

        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setRatingType(dto.getRatingType());
        quiz.setTimeLimitMinutes(dto.getTimeLimitMinutes());

        // Update questions if provided
        if (dto.getQuestionIds() != null) {
            quiz.getQuizQuestions().clear();
            int order = 1;
            for (Long questionId : dto.getQuestionIds()) {
                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));
                quiz.addQuestion(question, order++);
            }
        }

        quiz = quizRepository.save(quiz);
        return toFullDTO(quiz);
    }

    /**
     * Delete a quiz.
     */
    @Transactional
    public void deleteQuiz(Long id) {
        log.info("Deleting quiz: {}", id);
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz not found: " + id);
        }
        quizRepository.deleteById(id);
    }

    private QuizDTO toSummaryDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .ratingType(quiz.getRatingType())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .questionCount(quiz.getQuizQuestions().size())
                .build();
    }

    private QuizDTO toFullDTO(Quiz quiz) {
        List<QuestionDTO> questions = quiz.getQuizQuestions().stream()
                .map(qq -> questionService.getQuestionById(qq.getQuestion().getId()))
                .collect(Collectors.toList());

        return QuizDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .ratingType(quiz.getRatingType())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .questions(questions)
                .questionCount(questions.size())
                .build();
    }
}
