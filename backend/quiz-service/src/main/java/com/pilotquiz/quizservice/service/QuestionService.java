// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.pilotquiz.quizservice.dto.AnswerDTO;
import com.pilotquiz.quizservice.dto.BulkUploadResultDTO;
import com.pilotquiz.quizservice.dto.QuestionDTO;
import com.pilotquiz.quizservice.entity.Answer;
import com.pilotquiz.quizservice.entity.Category;
import com.pilotquiz.quizservice.entity.Question;
import com.pilotquiz.quizservice.exception.ResourceNotFoundException;
import com.pilotquiz.quizservice.repository.CategoryRepository;
import com.pilotquiz.quizservice.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for question operations including bulk CSV upload.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    /**
     * Get all questions with pagination.
     */
    public Page<QuestionDTO> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Get questions by rating type.
     */
    public Page<QuestionDTO> getQuestionsByRatingType(Question.RatingType ratingType, Pageable pageable) {
        return questionRepository.findByRatingType(ratingType, pageable).map(this::toDTO);
    }

    /**
     * Get questions by category.
     */
    public Page<QuestionDTO> getQuestionsByCategory(Long categoryId, Pageable pageable) {
        return questionRepository.findByCategoryId(categoryId, pageable).map(this::toDTO);
    }

    /**
     * Get question by ID.
     */
    public QuestionDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));
        return toDTO(question);
    }

    /**
     * Create a new question.
     */
    @Transactional
    public QuestionDTO createQuestion(QuestionDTO dto) {
        log.info("Creating question: {}",
                dto.getQuestionText().substring(0, Math.min(50, dto.getQuestionText().length())));

        Question question = toEntity(dto);
        question = questionRepository.save(question);

        log.info("Question created with ID: {}", question.getId());
        return toDTO(question);
    }

    /**
     * Update an existing question.
     */
    @Transactional
    public QuestionDTO updateQuestion(Long id, QuestionDTO dto) {
        log.info("Updating question: {}", id);

        Question existing = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));

        existing.setQuestionText(dto.getQuestionText());
        existing.setExplanation(dto.getExplanation());
        existing.setDifficulty(dto.getDifficulty());
        existing.setRatingType(dto.getRatingType());

        // Update answers
        existing.getAnswers().clear();
        if (dto.getAnswers() != null) {
            for (AnswerDTO answerDTO : dto.getAnswers()) {
                Answer answer = Answer.builder()
                        .answerText(answerDTO.getAnswerText())
                        .isCorrect(answerDTO.getIsCorrect())
                        .build();
                existing.addAnswer(answer);
            }
        }

        // Update categories
        if (dto.getCategoryIds() != null) {
            Set<Category> categories = dto.getCategoryIds().stream()
                    .map(catId -> categoryRepository.findById(catId)
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + catId)))
                    .collect(Collectors.toSet());
            existing.setCategories(categories);
        }

        existing = questionRepository.save(existing);
        return toDTO(existing);
    }

    /**
     * Delete a question.
     */
    @Transactional
    public void deleteQuestion(Long id) {
        log.info("Deleting question: {}", id);
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found: " + id);
        }
        questionRepository.deleteById(id);
    }

    /**
     * Bulk upload questions from CSV file.
     */
    @Transactional
    public BulkUploadResultDTO bulkUploadFromCsv(MultipartFile file) {
        log.info("Starting bulk upload from CSV: {}", file.getOriginalFilename());

        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();

            if (rows.isEmpty()) {
                errors.add("CSV file is empty");
                return BulkUploadResultDTO.builder()
                        .totalRows(0)
                        .successCount(0)
                        .errorCount(1)
                        .errors(errors)
                        .build();
            }

            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                totalRows++;
                String[] row = rows.get(i);

                try {
                    processCSVRow(row, i + 1);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }

        } catch (IOException | CsvException e) {
            log.error("Error reading CSV file", e);
            errors.add("Error reading CSV file: " + e.getMessage());
        }

        log.info("Bulk upload complete. Success: {}, Errors: {}", successCount, errors.size());

        return BulkUploadResultDTO.builder()
                .totalRows(totalRows)
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    /**
     * Process a single CSV row.
     * Expected format:
     * question_text,explanation,category,difficulty,rating_type,answer_1,answer_1_correct,...
     */
    private void processCSVRow(String[] row, int rowNum) {
        if (row.length < 7) {
            throw new IllegalArgumentException("Insufficient columns (need at least 7)");
        }

        String questionText = row[0].trim();
        String explanation = row[1].trim();
        String categoryName = row[2].trim();
        String difficultyStr = row[3].trim().toUpperCase();
        String ratingTypeStr = row[4].trim().toUpperCase();

        if (questionText.isEmpty()) {
            throw new IllegalArgumentException("Question text is required");
        }

        Question.Difficulty difficulty;
        try {
            difficulty = Question.Difficulty.valueOf(difficultyStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid difficulty: " + difficultyStr);
        }

        Question.RatingType ratingType;
        try {
            ratingType = Question.RatingType.valueOf(ratingTypeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid rating type: " + ratingTypeStr);
        }

        // Parse answers (pairs of text, correct flag)
        List<Answer> answers = new ArrayList<>();
        for (int i = 5; i < row.length - 1; i += 2) {
            String answerText = row[i].trim();
            if (answerText.isEmpty())
                continue;

            boolean isCorrect = i + 1 < row.length &&
                    ("true".equalsIgnoreCase(row[i + 1].trim()) || "1".equals(row[i + 1].trim()));

            answers.add(Answer.builder()
                    .answerText(answerText)
                    .isCorrect(isCorrect)
                    .build());
        }

        if (answers.isEmpty()) {
            throw new IllegalArgumentException("At least one answer is required");
        }

        // Find or create category
        Category category = categoryService.findOrCreateByName(categoryName);

        // Create question
        Question question = Question.builder()
                .questionText(questionText)
                .explanation(explanation)
                .difficulty(difficulty)
                .ratingType(ratingType)
                .categories(new HashSet<>(Collections.singletonList(category)))
                .build();

        for (Answer answer : answers) {
            question.addAnswer(answer);
        }

        questionRepository.save(question);
    }

    /**
     * Get CSV template content.
     */
    public String getCsvTemplate() {
        return "question_text,explanation,category,difficulty,rating_type,answer_1,answer_1_correct,answer_2,answer_2_correct,answer_3,answer_3_correct,answer_4,answer_4_correct\n"
                +
                "\"What is the minimum visibility for VFR in Class E?\",\"FAR 91.155 requires 3 SM\",Regulations,MEDIUM,PRIVATE,3 statute miles,true,1 statute mile,false,5 statute miles,false,2 statute miles,false";
    }

    private QuestionDTO toDTO(Question question) {
        List<AnswerDTO> answerDTOs = question.getAnswers().stream()
                .map(a -> AnswerDTO.builder()
                        .id(a.getId())
                        .answerText(a.getAnswerText())
                        .isCorrect(a.getIsCorrect())
                        .build())
                .collect(Collectors.toList());

        List<Long> categoryIds = question.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        return QuestionDTO.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .explanation(question.getExplanation())
                .difficulty(question.getDifficulty())
                .ratingType(question.getRatingType())
                .answers(answerDTOs)
                .categoryIds(categoryIds)
                .build();
    }

    private Question toEntity(QuestionDTO dto) {
        Question question = Question.builder()
                .questionText(dto.getQuestionText())
                .explanation(dto.getExplanation())
                .difficulty(dto.getDifficulty())
                .ratingType(dto.getRatingType())
                .build();

        if (dto.getAnswers() != null) {
            for (AnswerDTO answerDTO : dto.getAnswers()) {
                Answer answer = Answer.builder()
                        .answerText(answerDTO.getAnswerText())
                        .isCorrect(answerDTO.getIsCorrect())
                        .build();
                question.addAnswer(answer);
            }
        }

        if (dto.getCategoryIds() != null) {
            Set<Category> categories = dto.getCategoryIds().stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id)))
                    .collect(Collectors.toSet());
            question.setCategories(categories);
        }

        return question;
    }
}
