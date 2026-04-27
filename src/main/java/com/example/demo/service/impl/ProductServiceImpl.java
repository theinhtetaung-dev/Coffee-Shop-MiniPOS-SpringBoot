package com.example.demo.service.impl;

import com.example.demo.dto.product.ProductCreateRequest;
import com.example.demo.dto.product.ProductResponse;
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

        //Validating category exists
        if (request.getCategoryId() != null && !categoryRepository.existsById(request.getCategoryId())){
            throw new RuntimeException("Category not found");
        }
        productRepository.createProduct(request);
        return productRepository.getAllProducts().get(0);
    }

    //Getting all products
    @Override
    public List<ProductResponse> getAllProducts(){
        return productRepository.getAllProducts();
    }

    //Getting Product By Id
    @Override
    public ProductResponse getProductById(Integer id) {
        ProductResponse product = productRepository.getProductById(id);

        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        return product;
    }

    //Updating Product
    @Override
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductCreateRequest request) {

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }

        if (request.getCategoryId() != null &&
                !categoryRepository.existsById(request.getCategoryId())) {
            throw new RuntimeException("Category not found");
        }

        productRepository.updateProduct(id, request);
        return productRepository.getProductById(id);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id){
        if (!productRepository.existsById(id)){
            throw new RuntimeException("Product not found");
        }

        productRepository.deleteProduct(id);
    }
}
