// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.repository;

import com.pilotquiz.quizservice.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByRatingType(Question.RatingType ratingType, Pageable pageable);

    Page<Question> findByDifficulty(Question.Difficulty difficulty, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.categories c WHERE c.id = :categoryId")
    Page<Question> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.categories c WHERE c.id IN :categoryIds")
    Page<Question> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.ratingType = :ratingType ORDER BY RANDOM()")
    List<Question> findRandomByRatingType(@Param("ratingType") Question.RatingType ratingType, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.categories c WHERE c.id IN :categoryIds ORDER BY RANDOM()")
    List<Question> findRandomByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);
}
