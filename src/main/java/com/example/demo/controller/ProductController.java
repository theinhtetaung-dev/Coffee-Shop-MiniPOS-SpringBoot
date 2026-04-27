package com.example.demo.controller;

import com.example.demo.dto.product.ProductCreateRequest;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;


public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    public ProductResponse createProduct(@RequestBody ProductCreateRequest request){
        return productService.createProduct(request);
    }
    @GetMapping
    public List<ProductResponse> getAllProducts(){
        return productService.getAllProducts();
    }
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Integer id){
        return productService.getProductById(id);
    }
    @PutMapping
    public ProductResponse updateProduct(@PathVariable Integer id, @RequestBody ProductCreateRequest request){
        return productService.updateProduct(id, request);
    }
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Integer id){
         productService.deleteProduct(id);
         return "Product deleted Successfully.";
    }
}
