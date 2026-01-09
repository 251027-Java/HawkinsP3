// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.service;

import com.pilotquiz.quizservice.dto.CategoryDTO;
import com.pilotquiz.quizservice.entity.Category;
import com.pilotquiz.quizservice.exception.DuplicateResourceException;
import com.pilotquiz.quizservice.exception.ResourceNotFoundException;
import com.pilotquiz.quizservice.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Regulations")
                .description("FAA regulations")
                .build();
    }

    @Test
    @DisplayName("Should return all categories")
    void getAllCategories_Success() {
        // Given
        List<Category> categories = Arrays.asList(
                testCategory,
                Category.builder().id(2L).name("Weather").build());
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Regulations");
    }

    @Test
    @DisplayName("Should get category by ID")
    void getCategoryById_Success() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

        // When
        CategoryDTO result = categoryService.getCategoryById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Regulations");
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void getCategoryById_NotFound_ThrowsException() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.getCategoryById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create new category")
    void createCategory_Success() {
        // Given
        CategoryDTO dto = CategoryDTO.builder()
                .name("Navigation")
                .description("Navigation systems")
                .build();

        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(
                Category.builder().id(2L).name("Navigation").description("Navigation systems").build());

        // When
        CategoryDTO result = categoryService.createCategory(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Navigation");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when category name exists")
    void createCategory_DuplicateName_ThrowsException() {
        // Given
        CategoryDTO dto = CategoryDTO.builder().name("Regulations").build();
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> categoryService.createCategory(dto))
                .isInstanceOf(DuplicateResourceException.class);

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should find or create category by name")
    void findOrCreateByName_ExistingCategory() {
        // Given
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(testCategory));

        // When
        Category result = categoryService.findOrCreateByName("Regulations");

        // Then
        assertThat(result).isEqualTo(testCategory);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should create new category if not exists")
    void findOrCreateByName_NewCategory() {
        // Given
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        Category result = categoryService.findOrCreateByName("Regulations");

        // Then
        verify(categoryRepository).save(any(Category.class));
    }
}
