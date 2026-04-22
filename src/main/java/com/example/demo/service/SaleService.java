package com.example.demo.service;


import com.example.demo.dto.sale.SaleCreateRequest;
import com.example.demo.dto.sale.SaleResponse;

import java.util.List;

public interface SaleService {

    SaleResponse createSale(SaleCreateRequest request);

    List<SaleResponse> getAllSales();

    SaleResponse getSaleDetail(Integer saleId);

    SaleResponse updateSale(Integer saleId, SaleCreateRequest request);

    void deleteSale(Integer saleId);
}