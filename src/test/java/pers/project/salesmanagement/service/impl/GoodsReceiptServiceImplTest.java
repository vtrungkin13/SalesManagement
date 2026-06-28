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
import pers.project.salesmanagement.dto.request.CreateGoodsReceiptItemRequest;
import pers.project.salesmanagement.dto.request.CreateGoodsReceiptRequest;
import pers.project.salesmanagement.dto.response.GoodsReceiptResponse;
import pers.project.salesmanagement.entity.*;
import pers.project.salesmanagement.entity.status.PurchaseStatus;
import pers.project.salesmanagement.repository.*;
import pers.project.salesmanagement.security.TenantSecurityUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodsReceiptServiceImplTest {

    @Mock
    private GoodsReceiptRepository goodsReceiptRepository;
    @Mock
    private GoodsReceiptItemRepository goodsReceiptItemRepository;
    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ProductVariantRepository productVariantRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;
    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private GoodsReceiptServiceImpl goodsReceiptService;

    private UUID tenantId;
    private Tenant tenant;
    private PurchaseOrder purchaseOrder;
    private WareHouse warehouse;
    private ProductVariant variant;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        tenant = new Tenant();
        tenant.setId(tenantId);

        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(UUID.randomUUID());
        purchaseOrder.setTenant(tenant);
        purchaseOrder.setPoNumber("PO-1111");
        purchaseOrder.setStatus(PurchaseStatus.PENDING);

        warehouse = new WareHouse();
        warehouse.setId(UUID.randomUUID());
        warehouse.setTenant(tenant);
        warehouse.setName("Main Warehouse");

        variant = new ProductVariant();
        variant.setId(UUID.randomUUID());
        variant.setSku("SKU123");
        variant.setTenant(tenant);

        inventory = new Inventory();
        inventory.setWarehouse(warehouse);
        inventory.setVariant(variant);
        inventory.setQuantity(5);
    }

    @Test
    void testCreateGoodsReceipt_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            CreateGoodsReceiptItemRequest itemReq = new CreateGoodsReceiptItemRequest(variant.getId(), 10);
            CreateGoodsReceiptRequest request = new CreateGoodsReceiptRequest(
                    purchaseOrder.getId(), warehouse.getId(), Collections.singletonList(itemReq)
            );

            when(tenantRepository.getReferenceById(tenantId)).thenReturn(tenant);
            when(purchaseOrderRepository.findById(purchaseOrder.getId())).thenReturn(Optional.of(purchaseOrder));
            when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
            when(productVariantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));
            
            // Existing inventory found
            when(inventoryRepository.findByWarehouseIdAndVariantId(warehouse.getId(), variant.getId()))
                    .thenReturn(Optional.of(inventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

            GoodsReceipt gr = new GoodsReceipt();
            gr.setId(UUID.randomUUID());
            gr.setReceiptNumber("GR-12345");
            gr.setReceiptDate(LocalDateTime.now());
            gr.setPurchaseOrder(purchaseOrder);
            gr.setWarehouse(warehouse);

            GoodsReceiptItem item = new GoodsReceiptItem();
            item.setVariant(variant);
            item.setQuantity(10);
            gr.setGoodsReceiptItems(Collections.singletonList(item));

            when(goodsReceiptRepository.save(any(GoodsReceipt.class))).thenReturn(gr);

            GoodsReceiptResponse response = goodsReceiptService.createGoodsReceipt(request);

            assertNotNull(response);
            assertEquals("GR-12345", response.receiptNumber());
            assertEquals(15, inventory.getQuantity()); // 5 + 10 = 15
            assertEquals(PurchaseStatus.RECEIVED, purchaseOrder.getStatus()); // PO Status updated
            verify(inventoryTransactionRepository).save(any(InventoryTransaction.class));
            verify(goodsReceiptRepository).save(any(GoodsReceipt.class));
        }
    }
}
