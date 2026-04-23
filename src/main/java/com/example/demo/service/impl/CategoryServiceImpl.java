package com.example.demo.service.impl;

import com.example.demo.dto.category.CategoryCreateRequest;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service

public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request){
        Integer id = categoryRepository.createCategory(request);
        return categoryRepository.getCategoryById(id);
    }

    @Override
    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.getAllCategories();
    }

    @Override
    public CategoryResponse getCategoryById(Integer id){
        CategoryResponse category = categoryRepository.getCategoryById(id);

        if (category == null){
            throw new RuntimeException("Category not found with id: " + id);
        }
        return category;
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryCreateRequest request) {

        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        categoryRepository.updateCategory(id, request);
        return categoryRepository.getCategoryById(id);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id){
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteCategory(id);
    }

}
