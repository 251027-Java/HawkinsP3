// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QuestionResponse entity for individual question answers in an attempt.
 */
@Entity
@Table(name = "question_responses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @JsonIgnore
    private QuizAttempt quizAttempt;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "selected_answer_id")
    private Long selectedAnswerId;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "category_id")
    private Long categoryId;
}
