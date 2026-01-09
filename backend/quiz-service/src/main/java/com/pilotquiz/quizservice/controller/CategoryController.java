// Generated with assistance from Gemini AI
// Reviewed and modified by Richard Hawkins
package com.pilotquiz.quizservice.controller;

import com.pilotquiz.quizservice.dto.CategoryDTO;
import com.pilotquiz.quizservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for category operations.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Question category management")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories(
            @RequestParam(defaultValue = "false") boolean hierarchical) {

        List<CategoryDTO> categories;
        if (hierarchical) {
            categories = categoryService.getRootCategories();
        } else {
            categories = categoryService.getAllCategories();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @Operation(summary = "Create a new category (Admin only)")
    public ResponseEntity<CategoryDTO> createCategory(
            @Valid @RequestBody CategoryDTO request,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CategoryDTO category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
}
