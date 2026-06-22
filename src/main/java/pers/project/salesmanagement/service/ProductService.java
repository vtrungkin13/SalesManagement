package pers.project.salesmanagement.service;

import pers.project.salesmanagement.dto.request.CreateProductRequest;
import pers.project.salesmanagement.dto.response.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
}
