package com.example.demo.service;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.product.ProductCreateRequest;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.dto.product.ProductUpdateRequest;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    PagedResponse<ProductResponse> getAllProducts(int page, int size);

    ProductResponse getProductByCode(String code);

    ProductResponse updateProduct(ProductUpdateRequest request);

    void deleteProduct(String code);
}
