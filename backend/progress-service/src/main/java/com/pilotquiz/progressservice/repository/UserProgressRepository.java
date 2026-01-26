// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.progressservice.repository;

import com.pilotquiz.progressservice.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    List<UserProgress> findByUserIdOrderByTotalAttemptsDesc(Long userId);

    Optional<UserProgress> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query("SELECT up FROM UserProgress up WHERE up.userId = :userId ORDER BY " +
            "(CAST(up.correctCount AS double) / NULLIF(up.totalAttempts, 0)) ASC")
    List<UserProgress> findWeakAreasByUserId(@Param("userId") Long userId);

    @Query("SELECT up FROM UserProgress up WHERE up.userId = :userId ORDER BY " +
            "(CAST(up.correctCount AS double) / NULLIF(up.totalAttempts, 0)) DESC")
    List<UserProgress> findStrongAreasByUserId(@Param("userId") Long userId);
}
