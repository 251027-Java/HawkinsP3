// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilotquiz.progressservice.dto.QuestionResponseDTO;
import com.pilotquiz.progressservice.dto.QuizAttemptDTO;
import com.pilotquiz.progressservice.dto.UserAnalyticsDTO;
import com.pilotquiz.progressservice.dto.UserProgressDTO;
import com.pilotquiz.progressservice.exception.GlobalExceptionHandler;
import com.pilotquiz.progressservice.service.ProgressService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProgressControllerTest {

        private MockMvc mockMvc;

        @Mock
        private ProgressService progressService;

        @InjectMocks
        private ProgressController progressController;

        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                mockMvc = MockMvcBuilders.standaloneSetup(progressController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

        @Test
        @DisplayName("Should submit quiz attempt")
        void submitAttempt_Success() throws Exception {
                // Given
                QuizAttemptDTO request = QuizAttemptDTO.builder()
                                .quizId(1L)
                                .quizTitle("Test Quiz")
                                .responses(Collections.singletonList(
                                                QuestionResponseDTO.builder().questionId(1L).isCorrect(true).build()))
                                .build();

                QuizAttemptDTO response = QuizAttemptDTO.builder()
                                .id(1L)
                                .score(1)
                                .totalQuestions(1)
                                .build();

                when(progressService.submitAttempt(anyLong(), any(QuizAttemptDTO.class))).thenReturn(response);

                // When/Then
                mockMvc.perform(post("/api/v1/attempts")
                                .header("X-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.score").value(1));
        }

        @Test
        @DisplayName("Should return 400 when no user ID header")
        void submitAttempt_NoUserId_Returns400() throws Exception {
                // Given
                QuizAttemptDTO request = QuizAttemptDTO.builder()
                                .quizId(1L)
                                .responses(Collections.singletonList(
                                                QuestionResponseDTO.builder().questionId(1L).build()))
                                .build();

                // When/Then
                mockMvc.perform(post("/api/v1/attempts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should get user attempts")
        void getUserAttempts_Success() throws Exception {
                // Given
                Page<QuizAttemptDTO> page = new PageImpl<>(Collections.singletonList(
                                QuizAttemptDTO.builder().id(1L).score(8).build()));
                when(progressService.getUserAttempts(anyLong(), any(Pageable.class))).thenReturn(page);

                // When/Then
                mockMvc.perform(get("/api/v1/attempts")
                                .header("X-User-Id", "1"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should get user progress")
        void getUserProgress_Success() throws Exception {
                // Given
                when(progressService.getUserProgress(anyLong())).thenReturn(Collections.singletonList(
                                UserProgressDTO.builder().categoryName("Regulations").accuracyPercentage(70.0)
                                                .build()));

                // When/Then
                mockMvc.perform(get("/api/v1/progress")
                                .header("X-User-Id", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].categoryName").value("Regulations"));
        }

        @Test
        @DisplayName("Should get user analytics")
        void getUserAnalytics_Success() throws Exception {
                // Given
                UserAnalyticsDTO analytics = UserAnalyticsDTO.builder()
                                .userId(1L)
                                .totalQuizzesTaken(5L)
                                .overallAccuracy(75.0)
                                .build();
                when(progressService.getUserAnalytics(anyLong())).thenReturn(analytics);

                // When/Then
                mockMvc.perform(get("/api/v1/analytics")
                                .header("X-User-Id", "1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalQuizzesTaken").value(5))
                                .andExpect(jsonPath("$.overallAccuracy").value(75.0));
        }

        @Test
        @DisplayName("Should get weak areas")
        void getWeakAreas_Success() throws Exception {
                // Given
                when(progressService.getWeakAreas(anyLong(), anyInt())).thenReturn(Collections.singletonList(
                                UserProgressDTO.builder().categoryName("Weather").accuracyPercentage(50.0).build()));

                // When/Then
                mockMvc.perform(get("/api/v1/weak-areas")
                                .header("X-User-Id", "1")
                                .param("limit", "5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].categoryName").value("Weather"));
        }
}
