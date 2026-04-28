package com.example.demo.service.impl;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.product.ProductCreateRequest;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.dto.product.ProductUpdateRequest;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request){
        // Validating category exists
        if (!categoryRepository.existsById(request.getCategoryId())){
            throw new RuntimeException("Category not found with ID: " + request.getCategoryId());
        }

        Integer lastId = productRepository.getLastProductId();
        String productCode = String.format("P-%03d", lastId + 1);

        Integer newId = productRepository.createProduct(productCode, request);
        return productRepository.getProductById(newId);
    }

    @Override
    public PagedResponse<ProductResponse> getAllProducts(int page, int size){
        if (page < 0) throw new IllegalArgumentException("Page index must not be less than zero");
        if (size < 1) throw new IllegalArgumentException("Page size must not be less than one");

        List<ProductResponse> data = productRepository.getAllProducts(page, size);
        long total = productRepository.countAll();
        return new PagedResponse<>(data, page, size, total);
    }

    @Override
    public ProductResponse getProductByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Product code must not be blank");
        }
        ProductResponse product = productRepository.getProductByCode(code);
        if (product == null) {
            throw new RuntimeException("Product not found with code: " + code);
        }
        return product;
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(ProductUpdateRequest request) {
        if (!productRepository.existsByCode(request.getProductCode())) {
            throw new RuntimeException("Product not found with code: " + request.getProductCode());
        }

        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new RuntimeException("Category not found with ID: " + request.getCategoryId());
        }

        productRepository.updateProductByCode(request);
        return productRepository.getProductByCode(request.getProductCode());
    }

    @Override
    @Transactional
    public void deleteProduct(String code){
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Product code must not be blank");
        }
        if (!productRepository.existsByCode(code)){
            throw new RuntimeException("Product not found with code: " + code);
        }

        productRepository.deleteProductByCode(code);
    }
}
