package com.example.demo.service;

import com.example.demo.dto.product.ProductCreateRequest;
import com.example.demo.dto.product.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Integer id);

    ProductResponse updateProduct(Integer id, ProductCreateRequest request);

    void deleteProduct(Integer id);
}
