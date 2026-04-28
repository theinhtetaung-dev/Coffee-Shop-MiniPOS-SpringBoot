package com.example.demo.controller;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.product.ProductCodeRequest;
import com.example.demo.dto.product.ProductCreateRequest;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.dto.product.ProductUpdateRequest;
import com.example.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Manage system products. Product code is auto-generated as P-{N}.")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @Operation(summary = "Create a new product", description = "Product code is auto-generated. Provide product details in the body.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request){
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all products (paginated)", description = "Returns a paginated list. Default: page=0, size=10.")
    @ApiResponse(responseCode = "200", description = "Paginated list of products")
    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
            @Parameter(description = "Zero-based page index", example = "0", required = true) @RequestParam int page,
            @Parameter(description = "Number of records per page", example = "10", required = true) @RequestParam int size){
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @Operation(summary = "Find product by code", description = "Fetch a single product by its code.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/find")
    public ResponseEntity<ProductResponse> getProductByCode(@Valid @RequestBody ProductCodeRequest request){
        return ResponseEntity.ok(productService.getProductByCode(request.getProductCode()));
    }

    @Operation(summary = "Update product", description = "Update product details. Product code is required.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping
    public ResponseEntity<ProductResponse> updateProduct(@Valid @RequestBody ProductUpdateRequest request){
        return ResponseEntity.ok(productService.updateProduct(request));
    }

    @Operation(summary = "Delete product", description = "Delete a product by its code.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteProduct(@Valid @RequestBody ProductCodeRequest request){
         productService.deleteProduct(request.getProductCode());
         return ResponseEntity.ok("Product [" + request.getProductCode() + "] deleted successfully");
    }
}
