// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.repository;

import com.pilotquiz.progressservice.entity.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Page<QuizAttempt> findByUserIdOrderByCompletedAtDesc(Long userId, Pageable pageable);

    List<QuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT AVG(qa.score * 100.0 / qa.totalQuestions) FROM QuizAttempt qa WHERE qa.userId = :userId")
    Double getAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(qa.totalQuestions) FROM QuizAttempt qa WHERE qa.userId = :userId")
    Long getTotalQuestionsAttempted(@Param("userId") Long userId);
}
