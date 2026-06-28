package pers.project.salesmanagement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import pers.project.salesmanagement.dto.response.InventoryResponse;
import pers.project.salesmanagement.dto.response.InventoryStatsResponse;
import pers.project.salesmanagement.entity.*;
import pers.project.salesmanagement.mapper.InventoryMapper;
import pers.project.salesmanagement.repository.InventoryRepository;
import pers.project.salesmanagement.repository.ProductVariantRepository;
import pers.project.salesmanagement.security.TenantSecurityUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ProductVariantRepository productVariantRepository;
    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private UUID tenantId;
    private Tenant tenant;
    private WareHouse warehouse;
    private Product product;
    private ProductVariant variant;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        tenant = new Tenant();
        tenant.setId(tenantId);

        warehouse = new WareHouse();
        warehouse.setId(UUID.randomUUID());
        warehouse.setTenant(tenant);
        warehouse.setName("Main WH");

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Laptop");

        variant = new ProductVariant();
        variant.setId(UUID.randomUUID());
        variant.setSku("LAP-123");
        variant.setProduct(product);
        variant.setCostPrice(1000.0);
        variant.setSellPrice(1200.0);
        variant.setTenant(tenant);

        inventory = new Inventory();
        inventory.setId(UUID.randomUUID());
        inventory.setWarehouse(warehouse);
        inventory.setVariant(variant);
        inventory.setQuantity(10);
    }

    @Test
    void testGetInventoryLevels_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            Page<Inventory> page = new PageImpl<>(Collections.singletonList(inventory));
            when(inventoryRepository.findByTenantAndFilters(any(), any(), any(), any(), any())).thenReturn(page);
            when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(new InventoryResponse(
                    inventory.getId(), warehouse.getId(), warehouse.getName(), product.getId(),
                    product.getName(), variant.getId(), variant.getSku(), inventory.getQuantity(),
                    variant.getCostPrice(), variant.getSellPrice()
            ));

            Pageable pageable = PageRequest.of(0, 10);
            Page<InventoryResponse> result = inventoryService.getInventoryLevels(warehouse.getId(), null, "LAP",
                    pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("LAP-123", result.getContent().get(0).sku());
            assertEquals("Laptop", result.getContent().get(0).productName());
        }
    }

    @Test
    void testGetInventoryStats_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            when(inventoryRepository.findAllByTenantAndWarehouse(any(), any()))
                    .thenReturn(Collections.singletonList(inventory));
            when(productVariantRepository.countByTenantId(tenantId)).thenReturn(5L);

            InventoryStatsResponse stats = inventoryService.getInventoryStats(warehouse.getId(), 5);

            assertNotNull(stats);
            assertEquals(10L, stats.totalItems()); // quantity = 10
            assertEquals(1L, stats.totalUniqueVariants());
            assertEquals(10000.0, stats.totalCostValue()); // 10 * 1000 = 10000
            assertEquals(12000.0, stats.totalSellValue()); // 10 * 1200 = 12000
            assertEquals(0L, stats.lowStockCount()); // threshold = 5, quantity = 10
            assertEquals(4L, stats.outOfStockCount()); // total variants 5 - 1 in stock = 4
        }
    }
}
