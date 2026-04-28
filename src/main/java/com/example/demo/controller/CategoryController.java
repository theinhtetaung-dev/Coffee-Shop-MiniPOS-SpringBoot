package com.example.demo.controller;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.category.CategoryCodeRequest;
import com.example.demo.dto.category.CategoryCreateRequest;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.dto.category.CategoryUpdateRequest;
import com.example.demo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Manage product categories. Category code is auto-generated as C-{N}.")
public class CategoryController {

        private final CategoryService categoryService;

        public CategoryController(CategoryService categoryService) {
                this.categoryService = categoryService;
        }

        @Operation(summary = "Create a new category", description = "Category code is auto-generated . Provide categoryName and optional description in the body.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Category created successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation failed — check request body")
        })
        @PostMapping
        public ResponseEntity<CategoryResponse> createCategory(
                        @Valid @RequestBody CategoryCreateRequest request) {
                CategoryResponse response = categoryService.createCategory(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Get all categories (paginated)", description = "Returns a paginated list. Default: page=0, size=10.")
        @ApiResponse(responseCode = "200", description = "Paginated list of categories")
        @GetMapping
        public ResponseEntity<PagedResponse<CategoryResponse>> getAllCategories(
                        @Parameter(description = "Zero-based page index", example = "0", required = true) @RequestParam int page,
                        @Parameter(description = "Number of records per page", example = "10", required = true) @RequestParam int size) {
                return ResponseEntity.ok(categoryService.getAllCategories(page, size));
        }

        @Operation(summary = "Find category by code", description = "Fetch a single category. Provide { \"categoryCode\": \"C-1\" } in the request body.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Category found"),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        @PostMapping("/find")
        public ResponseEntity<CategoryResponse> getCategoryByCode(
                        @Valid @RequestBody CategoryCodeRequest request) {
                return ResponseEntity.ok(categoryService.getCategoryByCode(request.getCategoryCode()));
        }

        @Operation(summary = "Update category", description = "Provide { \"categoryCode\": \"C-1\", \"categoryName\": \"...\", \"description\": \"...\" } in the request body.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Category updated"),
                        @ApiResponse(responseCode = "400", description = "Validation failed or category code missing"),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        @PutMapping
        public ResponseEntity<CategoryResponse> updateCategory(
                        @Valid @RequestBody CategoryUpdateRequest request) {
                return ResponseEntity.ok(categoryService.updateCategory(request));
        }

        @Operation(summary = "Delete category", description = "Provide { \"categoryCode\": \"C-1\" } in the request body to delete that category.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Category deleted"),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        @DeleteMapping
        public ResponseEntity<String> deleteCategory(
                        @Valid @RequestBody CategoryCodeRequest request) {
                categoryService.deleteCategory(request.getCategoryCode());
                return ResponseEntity.ok("Category [" + request.getCategoryCode() + "] deleted successfully");
        }
}
