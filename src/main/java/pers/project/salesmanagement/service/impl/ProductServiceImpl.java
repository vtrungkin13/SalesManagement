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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;

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
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        return createProductInternal(request, tenantId);
    }

    private ProductResponse createProductInternal(CreateProductRequest request, UUID tenantId) {
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

        evictCache();

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> importProducts(List<CreateProductRequest> requests) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        // 1. Check for duplicate SKUs inside the import request list & validate basic constraints
        java.util.Set<String> batchSkus = new java.util.HashSet<>();
        List<UUID> categoryIds = new ArrayList<>();
        for (CreateProductRequest request : requests) {
            if (request.sku() == null || request.sku().isBlank()) {
                throw new RuntimeException("SKU cannot be blank");
            }
            if (!batchSkus.add(request.sku())) {
                throw new RuntimeException("Duplicate SKU found in import list: " + request.sku());
            }
            if (request.sellPrice() <= 0) {
                throw new RuntimeException("Price must be greater than zero");
            }
            if (request.categoryId() == null) {
                throw new RuntimeException("Category not found");
            }
            categoryIds.add(request.categoryId());
        }

        // 2. Batch check existing SKUs in DB
        List<String> existingSkus = productVariantRepository.findExistingSkus(tenantId, new ArrayList<>(batchSkus));
        if (!existingSkus.isEmpty()) {
            throw new RuntimeException("SKU already exists for this tenant");
        }

        // 3. Batch lookup categories and validate
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        java.util.Map<UUID, Category> categoryMap = new java.util.HashMap<>();
        for (Category category : categories) {
            if (category.getTenant() == null || !category.getTenant().getId().equals(tenantId)) {
                throw new RuntimeException("Category does not belong to the current tenant");
            }
            categoryMap.put(category.getId(), category);
        }

        // Ensure all categories exist
        for (UUID catId : categoryIds) {
            if (!categoryMap.containsKey(catId)) {
                throw new RuntimeException("Category not found");
            }
        }

        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        List<Product> productsToSave = new ArrayList<>();
        List<ProductVariant> variantsToSave = new ArrayList<>();

        // 4. Map requests to entities
        for (CreateProductRequest request : requests) {
            Category category = categoryMap.get(request.categoryId());
            
            // Map Product
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

            // Create Variant
            ProductVariant variant = new ProductVariant();
            variant.setSku(request.sku());
            variant.setCostPrice(request.costPrice());
            variant.setSellPrice(request.sellPrice());
            variant.setProduct(product);
            variant.setTenant(tenant);
            variant.setStatus(ProductStatus.ACTIVE);

            product.setVariants(List.of(variant));

            productsToSave.add(product);
            variantsToSave.add(variant);
        }

        // 5. Bulk Save
        List<Product> savedProducts = productRepository.saveAll(productsToSave);
        productVariantRepository.saveAll(variantsToSave);

        // 6. Evict cache once
        evictCache();

        // 7. Map to responses
        List<ProductResponse> responses = new ArrayList<>();
        for (Product savedProduct : savedProducts) {
            responses.add(productMapper.toResponse(savedProduct));
        }

        return responses;
    }

    @Override
    @Cacheable(
            value = "products",
            key = "T(pers.project.salesmanagement.security.TenantSecurityUtil).getCurrentTenantId() + " +
                  "':' + (#categoryId != null ? #categoryId : 'all') + " +
                  "':' + (#name != null ? #name : 'all') + " +
                  "':' + #pageable.pageNumber + " +
                  "':' + #pageable.pageSize + " +
                  "':' + #pageable.sort.toString()"
    )
    public Page<ProductResponse> getProducts(UUID categoryId, String name, Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        return productRepository.findByTenantAndFilters(tenantId, categoryId, name, pageable)
                .map(productMapper::toResponse);
    }

    private void evictCache() {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId != null) {
            try {
                String pattern = "products::" + tenantId + ":*";
                java.util.Set<String> keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            } catch (Exception e) {
                // Ignore cache errors
            }
        }
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

        return productMapper.toDetailResponse(product, currentInventory, price, sku);
    }
}
