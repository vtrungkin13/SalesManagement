package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateProductRequest;
import pers.project.salesmanagement.dto.response.ProductResponse;
import pers.project.salesmanagement.entity.Product;
import pers.project.salesmanagement.entity.ProductImage;
import pers.project.salesmanagement.mapper.ProductMapper;
import pers.project.salesmanagement.repository.ProductImageRepository;
import pers.project.salesmanagement.repository.ProductRepository;
import pers.project.salesmanagement.service.ProductService;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageRepository productImageRepository;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);

        ProductImage productImage = new ProductImage();
        productImage.setImageUrl(request.imageUrl());
        productImage.setProduct(product);

        product.setImage(productImage);
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }
}
