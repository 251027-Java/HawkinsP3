// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.userservice.controller;

import com.pilotquiz.userservice.dto.UserProfileDTO;
import com.pilotquiz.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user profile operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserProfileDTO> getProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // User ID comes from Gateway after JWT validation
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        UserProfileDTO profile = userService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody UserProfileDTO request) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        UserProfileDTO profile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (Admin only)")
    public ResponseEntity<UserProfileDTO> getUserById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        // Only admin can view other users
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        UserProfileDTO profile = userService.getUserById(id);
        return ResponseEntity.ok(profile);
    }
}
