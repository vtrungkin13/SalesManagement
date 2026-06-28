package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateProductRequest;
import pers.project.salesmanagement.dto.response.ProductDetailResponse;
import pers.project.salesmanagement.dto.response.ProductResponse;
import pers.project.salesmanagement.entity.Category;
import pers.project.salesmanagement.entity.Product;
import pers.project.salesmanagement.entity.ProductImage;
import pers.project.salesmanagement.entity.ProductVariant;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.entity.status.ProductStatus;
import pers.project.salesmanagement.mapper.ProductMapper;
import pers.project.salesmanagement.repository.CategoryRepository;
import pers.project.salesmanagement.repository.InventoryRepository;
import pers.project.salesmanagement.repository.ProductImageRepository;
import pers.project.salesmanagement.repository.ProductRepository;
import pers.project.salesmanagement.repository.ProductVariantRepository;
import pers.project.salesmanagement.repository.TenantRepository;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageRepository productImageRepository;
    private final TenantRepository tenantRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        // 1. Verify Category exists and belongs to current Tenant
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (category.getTenant() == null || !category.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Category does not belong to the current tenant");
        }

        // 2. Validate price > 0
        if (request.sellPrice() <= 0) {
            throw new RuntimeException("Price must be greater than zero");
        }

        // 3. Verify SKU is unique per tenant
        if (productVariantRepository.existsByTenantIdAndSku(tenantId, request.sku())) {
            throw new RuntimeException("SKU already exists for this tenant");
        }

        Tenant tenant = tenantRepository.getReferenceById(tenantId);

        // Create Product
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setTenant(tenant);
        product.setStatus(ProductStatus.ACTIVE);

        // Create Image
        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(request.imageUrl());
            productImage.setProduct(product);
            product.setImage(productImage);
        }

        // Create Default Variant
        ProductVariant variant = new ProductVariant();
        variant.setSku(request.sku());
        variant.setCostPrice(request.costPrice());
        variant.setSellPrice(request.sellPrice());
        variant.setProduct(product);
        variant.setTenant(tenant);
        variant.setStatus(ProductStatus.ACTIVE);

        List<ProductVariant> variants = new ArrayList<>();
        variants.add(variant);
        product.setVariants(variants);

        // Save
        Product savedProduct = productRepository.save(product);
        productVariantRepository.save(variant);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public Page<ProductResponse> getProducts(UUID categoryId, String name, Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        return productRepository.findByTenantAndFilters(tenantId, categoryId, name, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductDetailResponse getProductDetail(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Validate tenant access
        if (product.getTenant() == null || !product.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Access denied to this product");
        }

        ProductDetailResponse response = productMapper.toDetailResponse(product);

        // Fetch inventories sum
        int currentInventory = inventoryRepository.sumQuantityByProductId(id);

        // Find main variant price and SKU
        double price = 0.0;
        String sku = "";
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            ProductVariant firstVariant = product.getVariants().get(0);
            price = firstVariant.getSellPrice();
            sku = firstVariant.getSku();
        }

        return new ProductDetailResponse(
                response.id(),
                response.code(),
                response.name(),
                response.description(),
                response.imageUrl(),
                response.categoryName(),
                currentInventory,
                price,
                sku);
    }
}
