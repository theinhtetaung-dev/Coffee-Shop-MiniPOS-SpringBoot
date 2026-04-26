package com.example.demo.service.impl;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.category.CategoryCreateRequest;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ─── Create with auto-generated code "C-{lastId+1}" ─────────────────────
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Integer lastId = categoryRepository.getLastCategoryId();
        String categoryCode = "C-" + (lastId + 1);

        Integer newId = categoryRepository.createCategory(categoryCode, request);
        return categoryRepository.getCategoryById(newId);
    }

    // ─── Get all paginated ────────────────────────────────────────────────────
    @Override
    public PagedResponse<CategoryResponse> getAllCategories(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("Page index must not be less than zero");
        if (size < 1) throw new IllegalArgumentException("Page size must not be less than one");

        List<CategoryResponse> data = categoryRepository.getAllCategories(page, size);
        long total = categoryRepository.countAll();
        return new PagedResponse<>(data, page, size, total);
    }

    // ─── Get by Code ──────────────────────────────────────────────────────────
    @Override
    public CategoryResponse getCategoryByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Category code must not be blank");
        }
        CategoryResponse category = categoryRepository.getCategoryByCode(code);
        if (category == null) {
            throw new RuntimeException("Category not found with code: " + code);
        }
        return category;
    }

    // ─── Update by Code ───────────────────────────────────────────────────────
    @Override
    @Transactional
    public CategoryResponse updateCategory(String code, CategoryCreateRequest request) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Category code must not be blank");
        }
        if (!categoryRepository.existsByCode(code)) {
            throw new RuntimeException("Category not found with code: " + code);
        }

        categoryRepository.updateCategoryByCode(code, request);
        return categoryRepository.getCategoryByCode(code);
    }

    // ─── Delete by Code ───────────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteCategory(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Category code must not be blank");
        }
        if (!categoryRepository.existsByCode(code)) {
            throw new RuntimeException("Category not found with code: " + code);
        }
        categoryRepository.deleteCategoryByCode(code);
    }
 }
