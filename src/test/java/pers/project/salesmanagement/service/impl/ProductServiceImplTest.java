package pers.project.salesmanagement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreateProductRequest;
import pers.project.salesmanagement.dto.response.ProductDetailResponse;
import pers.project.salesmanagement.dto.response.ProductResponse;
import pers.project.salesmanagement.entity.Category;
import pers.project.salesmanagement.entity.Product;
import pers.project.salesmanagement.entity.ProductImage;
import pers.project.salesmanagement.entity.ProductVariant;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.mapper.ProductMapper;
import pers.project.salesmanagement.repository.CategoryRepository;
import pers.project.salesmanagement.repository.InventoryRepository;
import pers.project.salesmanagement.repository.ProductImageRepository;
import pers.project.salesmanagement.repository.ProductRepository;
import pers.project.salesmanagement.repository.ProductVariantRepository;
import pers.project.salesmanagement.repository.TenantRepository;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductVariantRepository productVariantRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID tenantId;
    private Tenant tenant;
    private Category category;
    private CreateProductRequest createProductRequest;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        tenant = new Tenant();
        tenant.setId(tenantId);

        category = new Category();
        category.setId(UUID.randomUUID());
        category.setTenant(tenant);

        createProductRequest = new CreateProductRequest(
                "PROD1", "Test Product", "Description", "http://image.url", category.getId(), "SKU123", 100.0, 50.0);
    }

    @Test
    void testCreateProduct_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
            when(productVariantRepository.existsByTenantIdAndSku(tenantId, "SKU123")).thenReturn(false);
            when(tenantRepository.getReferenceById(tenantId)).thenReturn(tenant);

            Product product = new Product();
            product.setName("Test Product");
            when(productMapper.toEntity(any())).thenReturn(product);
            when(productRepository.save(any())).thenReturn(product);

            ProductResponse expectedResponse = new ProductResponse(UUID.randomUUID(), "PROD1", "Test Product",
                    "Description", "http://image.url", "CategoryName");
            when(productMapper.toResponse(any())).thenReturn(expectedResponse);

            ProductResponse response = productService.createProduct(createProductRequest);

            assertNotNull(response);
            assertEquals("Test Product", response.name());
            verify(productRepository).save(any(Product.class));
            verify(productVariantRepository).save(any(ProductVariant.class));
        }
    }

    @Test
    void testCreateProduct_InvalidPrice() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);
            when(categoryRepository.findById(any())).thenReturn(Optional.of(category));

            CreateProductRequest invalidRequest = new CreateProductRequest(
                    "PROD1", "Test Product", "Description", "http://image.url", category.getId(), "SKU123", -10.0,
                    50.0);

            Exception exception = assertThrows(RuntimeException.class,
                    () -> productService.createProduct(invalidRequest));
            assertEquals("Price must be greater than zero", exception.getMessage());
        }
    }

    @Test
    void testCreateProduct_DuplicateSku() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);
            when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
            when(productVariantRepository.existsByTenantIdAndSku(tenantId, "SKU123")).thenReturn(true);

            Exception exception = assertThrows(RuntimeException.class,
                    () -> productService.createProduct(createProductRequest));
            assertEquals("SKU already exists for this tenant", exception.getMessage());
        }
    }

    @Test
    void testGetProducts_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            Product product = new Product();
            product.setName("Test Product");
            Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));

            when(productRepository.findByTenantAndFilters(eq(tenantId), any(), any(), any())).thenReturn(productPage);
            when(productMapper.toResponse(any())).thenReturn(new ProductResponse(UUID.randomUUID(), "PROD1",
                    "Test Product", "Description", "http://image.url", "CategoryName"));

            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductResponse> result = productService.getProducts(category.getId(), "Test", pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("Test Product", result.getContent().get(0).name());
        }
    }

    @Test
    void testGetProductDetail_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setTenant(tenant);

            ProductVariant variant = new ProductVariant();
            variant.setSku("SKU123");
            variant.setSellPrice(100.0);
            product.setVariants(Collections.singletonList(variant));

            when(productRepository.findById(any())).thenReturn(Optional.of(product));
            when(inventoryRepository.sumQuantityByProductId(any())).thenReturn(150);
            when(productMapper.toDetailResponse(any(), anyInt(), anyDouble(), anyString()))
                    .thenReturn(new ProductDetailResponse(product.getId(), "PROD1",
                            "Test Product", "Description", "http://image.url", "CategoryName", 150, 100.0, "SKU123"));

            ProductDetailResponse detail = productService.getProductDetail(product.getId());

            assertNotNull(detail);
            assertEquals(150, detail.currentInventory());
            assertEquals(100.0, detail.price());
            assertEquals("SKU123", detail.sku());
        }
    }

    @Test
    void testImportProducts_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
            when(productVariantRepository.existsByTenantIdAndSku(eq(tenantId), anyString())).thenReturn(false);
            when(tenantRepository.getReferenceById(tenantId)).thenReturn(tenant);

            Product product = new Product();
            product.setName("Test Product");
            when(productMapper.toEntity(any())).thenReturn(product);
            when(productRepository.save(any())).thenReturn(product);

            ProductResponse expectedResponse = new ProductResponse(UUID.randomUUID(), "PROD1", "Test Product",
                    "Description", "http://image.url", "CategoryName");
            when(productMapper.toResponse(any())).thenReturn(expectedResponse);

            CreateProductRequest req1 = new CreateProductRequest(
                    "PROD1", "Test Product 1", "Desc", "url", category.getId(), "SKU1", 100.0, 50.0);
            CreateProductRequest req2 = new CreateProductRequest(
                    "PROD2", "Test Product 2", "Desc", "url", category.getId(), "SKU2", 200.0, 150.0);

            List<ProductResponse> responses = productService.importProducts(List.of(req1, req2));

            assertNotNull(responses);
            assertEquals(2, responses.size());
            verify(productRepository, times(2)).save(any(Product.class));
            verify(productVariantRepository, times(2)).save(any(ProductVariant.class));
        }
    }

    @Test
    void testImportProducts_DuplicateSkuInBatch() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            CreateProductRequest req1 = new CreateProductRequest(
                    "PROD1", "Test Product 1", "Desc", "url", category.getId(), "SKU_DUP", 100.0, 50.0);
            CreateProductRequest req2 = new CreateProductRequest(
                    "PROD2", "Test Product 2", "Desc", "url", category.getId(), "SKU_DUP", 200.0, 150.0);

            Exception exception = assertThrows(RuntimeException.class,
                    () -> productService.importProducts(List.of(req1, req2)));
            assertTrue(exception.getMessage().contains("Duplicate SKU found in import list"));
        }
    }

    @Test
    void testImportProducts_DuplicateSkuInDb() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
            when(productVariantRepository.existsByTenantIdAndSku(tenantId, "SKU1")).thenReturn(true);

            CreateProductRequest req1 = new CreateProductRequest(
                    "PROD1", "Test Product 1", "Desc", "url", category.getId(), "SKU1", 100.0, 50.0);

            Exception exception = assertThrows(RuntimeException.class,
                    () -> productService.importProducts(List.of(req1)));
            assertEquals("SKU already exists for this tenant", exception.getMessage());
        }
    }

    @Test
    void testCreateProduct_EvictsCache() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
            when(tenantRepository.getReferenceById(tenantId)).thenReturn(tenant);
            when(productVariantRepository.existsByTenantIdAndSku(tenantId, "SKU123")).thenReturn(false);

            Product product = new Product();
            product.setName("Test Product");
            when(productMapper.toEntity(any())).thenReturn(product);
            when(productRepository.save(any())).thenReturn(product);
            when(productMapper.toResponse(any())).thenReturn(new ProductResponse(UUID.randomUUID(), "PROD1", "Test Product", "Desc", "url", "Category"));

            java.util.Set<String> keys = new java.util.HashSet<>(List.of("products::" + tenantId + ":key1"));
            when(redisTemplate.keys("products::" + tenantId + ":*")).thenReturn(keys);

            productService.createProduct(createProductRequest);

            verify(redisTemplate).keys("products::" + tenantId + ":*");
            verify(redisTemplate).delete(keys);
        }
    }
}
