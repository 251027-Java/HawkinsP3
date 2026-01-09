// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilotquiz.quizservice.dto.AnswerDTO;
import com.pilotquiz.quizservice.dto.QuestionDTO;
import com.pilotquiz.quizservice.entity.Question;
import com.pilotquiz.quizservice.exception.GlobalExceptionHandler;
import com.pilotquiz.quizservice.exception.ResourceNotFoundException;
import com.pilotquiz.quizservice.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

        private MockMvc mockMvc;

        @Mock
        private QuestionService questionService;

        @InjectMocks
        private QuestionController questionController;

        private ObjectMapper objectMapper;
        private QuestionDTO testQuestionDTO;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                mockMvc = MockMvcBuilders.standaloneSetup(questionController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

                testQuestionDTO = QuestionDTO.builder()
                                .id(1L)
                                .questionText("What is the minimum visibility for VFR?")
                                .difficulty(Question.Difficulty.MEDIUM)
                                .ratingType(Question.RatingType.PRIVATE)
                                .answers(Collections.singletonList(AnswerDTO.builder()
                                                .id(1L)
                                                .answerText("3 statute miles")
                                                .isCorrect(true)
                                                .build()))
                                .build();
        }

        @Test
        @DisplayName("Should get all questions")
        void getAllQuestions_Success() throws Exception {
                // Given
                Page<QuestionDTO> page = new PageImpl<>(Collections.singletonList(testQuestionDTO));
                when(questionService.getAllQuestions(any(Pageable.class))).thenReturn(page);

                // When/Then
                mockMvc.perform(get("/api/v1/questions"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].questionText").exists());
        }

        @Test
        @DisplayName("Should get question by ID")
        void getQuestionById_Success() throws Exception {
                // Given
                when(questionService.getQuestionById(anyLong())).thenReturn(testQuestionDTO);

                // When/Then
                mockMvc.perform(get("/api/v1/questions/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.questionText").value(testQuestionDTO.getQuestionText()));
        }

        @Test
        @DisplayName("Should return 404 when question not found")
        void getQuestionById_NotFound() throws Exception {
                // Given
                when(questionService.getQuestionById(anyLong()))
                                .thenThrow(new ResourceNotFoundException("Question not found"));

                // When/Then
                mockMvc.perform(get("/api/v1/questions/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should create question with admin role")
        void createQuestion_Admin_Success() throws Exception {
                // Given
                when(questionService.createQuestion(any(QuestionDTO.class))).thenReturn(testQuestionDTO);

                // When/Then
                mockMvc.perform(post("/api/v1/questions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Role", "ROLE_ADMIN")
                                .content(objectMapper.writeValueAsString(testQuestionDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.questionText").value(testQuestionDTO.getQuestionText()));
        }

        @Test
        @DisplayName("Should get CSV template")
        void getTemplate_Success() throws Exception {
                // Given
                when(questionService.getCsvTemplate()).thenReturn("question_text,difficulty...");

                // When/Then
                mockMvc.perform(get("/api/v1/questions/template"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith("text/csv"));
        }
}
