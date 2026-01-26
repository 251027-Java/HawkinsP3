package com.pilotquiz.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilotquiz.userservice.config.SecurityConfig;
import com.pilotquiz.userservice.dto.UserProfileDTO;
import com.pilotquiz.userservice.entity.PilotProfile;
import com.pilotquiz.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("Should return user profile when authenticated")
        void getProfile_Success() throws Exception {
                // Given
                Long userId = 1L;
                UserProfileDTO profile = UserProfileDTO.builder()
                                .id(userId)
                                .email("test@example.com")
                                .firstName("John")
                                .lastName("Doe")
                                .currentRating(PilotProfile.Rating.PRIVATE)
                                .build();

                when(userService.getProfile(userId)).thenReturn(profile);

                // When/Then
                mockMvc.perform(get("/api/v1/users/profile")
                                .header("X-User-Id", userId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("test@example.com"))
                                .andExpect(jsonPath("$.currentRating").value("PRIVATE"));
        }

        @Test
        @DisplayName("Should return 400 when X-User-Id header is missing")
        void getProfile_MissingHeader_Returns400() throws Exception {
                // When/Then
                mockMvc.perform(get("/api/v1/users/profile")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should update user profile successfully")
        void updateProfile_Success() throws Exception {
                // Given
                Long userId = 1L;
                UserProfileDTO request = UserProfileDTO.builder()
                                .firstName("Jane")
                                .lastName("Smith")
                                .build();

                UserProfileDTO updatedProfile = UserProfileDTO.builder()
                                .id(userId)
                                .email("test@example.com")
                                .firstName("Jane")
                                .lastName("Smith")
                                .build();

                when(userService.updateProfile(eq(userId), any(UserProfileDTO.class))).thenReturn(updatedProfile);

                // When/Then
                mockMvc.perform(put("/api/v1/users/profile")
                                .with(csrf())
                                .header("X-User-Id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName").value("Jane"));
        }

        @Test
        @DisplayName("Should allow admin to view any user")
        void getUserById_Admin_Success() throws Exception {
                // Given
                Long targetId = 2L;
                UserProfileDTO profile = UserProfileDTO.builder()
                                .id(targetId)
                                .email("other@example.com")
                                .build();

                when(userService.getUserById(targetId)).thenReturn(profile);

                // When/Then
                mockMvc.perform(get("/api/v1/users/{id}", targetId)
                                .header("X-User-Role", "ROLE_ADMIN")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("other@example.com"));
        }

        @Test
        @DisplayName("Should forbid non-admin from viewing other users")
        void getUserById_User_Returns403() throws Exception {
                // Given
                Long targetId = 2L;

                // When/Then
                mockMvc.perform(get("/api/v1/users/{id}", targetId)
                                .header("X-User-Role", "ROLE_USER")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isForbidden());
        }
}
