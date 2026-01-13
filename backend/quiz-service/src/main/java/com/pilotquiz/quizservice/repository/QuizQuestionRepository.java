package com.pilotquiz.quizservice.repository;

import com.pilotquiz.quizservice.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    @Modifying
    @Query("DELETE FROM QuizQuestion qq WHERE qq.question.id = :questionId")
    void deleteByQuestionId(Long questionId);
}
