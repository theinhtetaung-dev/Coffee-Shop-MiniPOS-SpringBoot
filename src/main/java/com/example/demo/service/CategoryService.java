package com.example.demo.service;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.category.CategoryCreateRequest;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.dto.category.CategoryUpdateRequest;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request);

    PagedResponse<CategoryResponse> getAllCategories(int page, int size);

    CategoryResponse getCategoryByCode(String code);

    CategoryResponse updateCategory(CategoryUpdateRequest request);

    void deleteCategory(String code);
}
