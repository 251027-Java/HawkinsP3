// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.repository;

import com.pilotquiz.userservice.entity.PilotProfile;
import com.pilotquiz.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PilotProfile entity operations.
 */
@Repository
public interface PilotProfileRepository extends JpaRepository<PilotProfile, Long> {

    Optional<PilotProfile> findByUser(User user);

    Optional<PilotProfile> findByUserId(Long userId);
}
