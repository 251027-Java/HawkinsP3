// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.service;

import com.pilotquiz.quizservice.dto.CategoryDTO;
import com.pilotquiz.quizservice.entity.Category;
import com.pilotquiz.quizservice.exception.DuplicateResourceException;
import com.pilotquiz.quizservice.exception.ResourceNotFoundException;
import com.pilotquiz.quizservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for category operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Get all categories as a flat list.
     */
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get root categories with children.
     */
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentIsNull().stream()
                .map(this::toDTOWithChildren)
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID.
     */
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        return toDTOWithChildren(category);
    }

    /**
     * Create a new category.
     */
    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        log.info("Creating category: {}", dto.getName());

        if (categoryRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Category already exists: " + dto.getName());
        }

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Parent category not found: " + dto.getParentId()));
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        log.info("Category created with ID: {}", category.getId());

        return toDTO(category);
    }

    /**
     * Find or create a category by name.
     */
    @Transactional
    public Category findOrCreateByName(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category category = Category.builder()
                            .name(name)
                            .build();
                    return categoryRepository.save(category);
                });
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }

    private CategoryDTO toDTOWithChildren(Category category) {
        CategoryDTO dto = toDTO(category);
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            dto.setChildren(category.getChildren().stream()
                    .map(this::toDTOWithChildren)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
