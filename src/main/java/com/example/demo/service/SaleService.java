package com.example.demo.service;

import com.example.demo.dto.PagedResponse;
import com.example.demo.dto.sale.SaleCreateRequest;
import com.example.demo.dto.sale.SaleResponse;
import com.example.demo.dto.sale.SaleUpdateRequest;

public interface SaleService {

    SaleResponse createSale(SaleCreateRequest request);

    PagedResponse<SaleResponse> getAllSales(int page, int size);

    SaleResponse getSaleByCode(String saleCode);

    SaleResponse updateSale(SaleUpdateRequest request);

    void deleteSale(String saleCode);
}
