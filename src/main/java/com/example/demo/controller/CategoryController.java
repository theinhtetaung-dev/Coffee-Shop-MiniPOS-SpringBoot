package com.example.demo.controller;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.category.CategoryCreateRequest;
import com.example.demo.dto.category.CategoryResponse;
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

    @Operation(
            summary = "Create a new category",
            description = "Category code is auto-generated as 'C-{lastId+1}'. You only need to provide categoryName and description."
    )
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

    @Operation(
            summary = "Get all categories (paginated)",
            description = "Returns a paginated list. Default: page=0, size=10."
    )
    @ApiResponse(responseCode = "200", description = "Paginated list of categories")
    @GetMapping
    public ResponseEntity<PagedResponse<CategoryResponse>> getAllCategories(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.getAllCategories(page, size));
    }

    @Operation(
            summary = "Get category by code",
            description = "Fetch a single category using its auto-generated code, e.g. 'C-1'."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<CategoryResponse> getCategoryByCode(
            @Parameter(description = "Category code, e.g. C-1", example = "C-1")
            @PathVariable String code) {
        return ResponseEntity.ok(categoryService.getCategoryByCode(code));
    }

    @Operation(
            summary = "Update category by code",
            description = "Update categoryName or description using the category code. The code itself never changes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/code/{code}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category code, e.g. C-1", example = "C-1")
            @PathVariable String code,
            @Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(code, request));
    }

    @Operation(
            summary = "Delete category by code",
            description = "Permanently delete a category using its code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/code/{code}")
    public ResponseEntity<String> deleteCategory(
            @Parameter(description = "Category code, e.g. C-1", example = "C-1")
            @PathVariable String code) {
        categoryService.deleteCategory(code);
        return ResponseEntity.ok("Category [" + code + "] deleted successfully");
    }
}
