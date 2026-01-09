// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz entity representing a collection of questions.
 */
@Entity
@Table(name = "quizzes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_type", nullable = false)
    private Question.RatingType ratingType;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    @Builder.Default
    private List<QuizQuestion> quizQuestions = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Add a question to this quiz with order.
     */
    public void addQuestion(Question question, int order) {
        QuizQuestion qq = QuizQuestion.builder()
                .quiz(this)
                .question(question)
                .questionOrder(order)
                .build();
        quizQuestions.add(qq);
    }
}
