package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreateProductRequest;
import pers.project.salesmanagement.dto.response.ProductDetailResponse;
import pers.project.salesmanagement.dto.response.ProductResponse;

import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    Page<ProductResponse> getProducts(UUID categoryId, String name, Pageable pageable);
    ProductDetailResponse getProductDetail(UUID id);
}
