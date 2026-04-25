package com.example.demo.controller;

import com.example.demo.dto.category.CategoryCreateRequest;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

public  CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
}

@PostMapping
public CategoryResponse createCategory(@RequestBody CategoryCreateRequest request) {
    return categoryService.createCategory(request);
}

@GetMapping
public List<CategoryResponse> getAllCategories() {
    return categoryService.getAllCategories();
}

@GetMapping("/{id}")
public CategoryResponse getCategoryById(@PathVariable Integer id) {
    return categoryService.getCategoryById(id);
}

@PutMapping("/{id}")
public CategoryResponse updateCategory(@PathVariable Integer id,
                                       @RequestBody CategoryCreateRequest request) {
    return categoryService.updateCategory(id, request);
}

@DeleteMapping("/{id}")
public String deleteCategory(@PathVariable Integer id) {
    categoryService.deleteCategory(id);
    return "Category deleted successfully";
    }

}
