package com.example.demo.service;
import com.example.demo.dto.category.CategoryCreateRequest;
import com.example.demo.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request);

    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Integer id);

    CategoryResponse updateCategory(Integer id, CategoryCreateRequest request);

    void deleteCategory(Integer id);
}
