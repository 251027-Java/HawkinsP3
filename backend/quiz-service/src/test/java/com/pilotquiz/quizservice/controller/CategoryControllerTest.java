package com.pilotquiz.quizservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilotquiz.quizservice.dto.CategoryDTO;
import com.pilotquiz.quizservice.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should get all categories successfully")
    void getAllCategories_Success() throws Exception {
        // Given
        CategoryDTO category = CategoryDTO.builder()
                .id(1L)
                .name("General")
                .build();

        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        // When/Then
        mockMvc.perform(get("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("General"));
    }

    @Test
    @DisplayName("Should get category by ID")
    void getCategoryById_Success() throws Exception {
        // Given
        Long categoryId = 1L;
        CategoryDTO category = CategoryDTO.builder()
                .id(categoryId)
                .name("Navigation")
                .build();

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        // When/Then
        mockMvc.perform(get("/api/v1/categories/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Navigation"));
    }

    @Test
    @DisplayName("Should create category when admin")
    void createCategory_Admin_Success() throws Exception {
        // Given
        CategoryDTO request = CategoryDTO.builder()
                .name("Meteorology")
                .description("Weather patterns")
                .build();

        CategoryDTO response = CategoryDTO.builder()
                .id(1L)
                .name("Meteorology")
                .build();

        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/v1/categories")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should forbid create category when non-admin")
    void createCategory_NonAdmin_Forbidden() throws Exception {
        // Given - valid request
        CategoryDTO request = CategoryDTO.builder()
                .name("Restricted")
                .build();

        // When/Then
        mockMvc.perform(post("/api/v1/categories")
                .header("X-User-Role", "ROLE_USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
