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
import pers.project.salesmanagement.dto.request.CreateSalesOrderItemRequest;
import pers.project.salesmanagement.dto.request.CreateSalesOrderRequest;
import pers.project.salesmanagement.dto.request.UpdateSalesOrderRequest;
import pers.project.salesmanagement.dto.response.SalesOrderItemResponse;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;
import pers.project.salesmanagement.entity.*;
import pers.project.salesmanagement.mapper.SalesOrderMapper;
import pers.project.salesmanagement.repository.*;
import pers.project.salesmanagement.security.TenantSecurityUtil;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceImplTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductVariantRepository productVariantRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private SalesOrderItemRepository salesOrderItemRepository;
    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;
    @Mock
    private SalesOrderMapper salesOrderMapper;
    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private SalesOrderServiceImpl salesOrderService;

    private UUID tenantId;
    private Tenant tenant;
    private Customer customer;
    private ProductVariant variant;
    private Inventory inventory;

    private WareHouse warehouse;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        tenant = new Tenant();
        tenant.setId(tenantId);

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setTenant(tenant);
        customer.setName("John Doe");

        warehouse = new WareHouse();
        warehouse.setId(UUID.randomUUID());
        warehouse.setTenant(tenant);

        variant = new ProductVariant();
        variant.setId(UUID.randomUUID());
        variant.setSku("SKU123");
        variant.setSellPrice(150.0);
        variant.setTenant(tenant);

        inventory = new Inventory();
        inventory.setWarehouse(warehouse);
        inventory.setVariant(variant);
        inventory.setQuantity(10);
    }

    @Test
    void testCreateSalesOrder_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            CreateSalesOrderItemRequest itemReq = new CreateSalesOrderItemRequest(variant.getId(), 2, 10.0);
            CreateSalesOrderRequest request = new CreateSalesOrderRequest(customer.getId(), warehouse.getId(),
                    Collections.singletonList(itemReq));

            when(tenantRepository.getReferenceById(tenantId)).thenReturn(tenant);
            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
            when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
            when(productVariantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));
            when(inventoryRepository.findByWarehouseIdAndVariantId(warehouse.getId(), variant.getId()))
                    .thenReturn(Optional.of(inventory));

            SalesOrder mockOrder = new SalesOrder();
            mockOrder.setId(UUID.randomUUID());
            mockOrder.setOrderNumber("SO-12345");
            mockOrder.setSubtotal(300.0);
            mockOrder.setDiscount(10.0);
            mockOrder.setTotal(290.0);
            mockOrder.setCreatedAt(LocalDateTime.now());
            mockOrder.setCustomer(customer);

            SalesOrderItem item = new SalesOrderItem();
            item.setVariant(variant);
            item.setQuantity(2);
            item.setUnitPrice(150.0);
            item.setDiscount(10.0);
            mockOrder.setSalesOrderItems(Collections.singletonList(item));

            when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(mockOrder);

            SalesOrderResponse mockResponse = new SalesOrderResponse(mockOrder.getId(), "SO-12345", 300.0, 10.0, 290.0,
                    mockOrder.getCreatedAt(), "John Doe", new ArrayList<>());
            when(salesOrderMapper.toResponse(any(SalesOrder.class))).thenReturn(mockResponse);

            SalesOrderResponse response = salesOrderService.createSalesOrder(request);

            assertNotNull(response);
            assertEquals("SO-12345", response.orderNumber());
            assertEquals(290.0, response.total());
            assertEquals(8, inventory.getQuantity()); // Stock deducted by 2
            verify(inventoryRepository).save(inventory);
        }
    }

    @Test
    void testCreateSalesOrder_OutOfStock() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            CreateSalesOrderItemRequest itemReq = new CreateSalesOrderItemRequest(variant.getId(), 20, 10.0); // requests 20, stock is 10
            CreateSalesOrderRequest request = new CreateSalesOrderRequest(customer.getId(), warehouse.getId(),
                    Collections.singletonList(itemReq));

            when(tenantRepository.getReferenceById(tenantId)).thenReturn(tenant);
            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
            when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
            when(productVariantRepository.findById(variant.getId())).thenReturn(Optional.of(variant));
            when(inventoryRepository.findByWarehouseIdAndVariantId(warehouse.getId(), variant.getId()))
                    .thenReturn(Optional.of(inventory));

            Exception exception = assertThrows(RuntimeException.class,
                    () -> salesOrderService.createSalesOrder(request));
            assertTrue(exception.getMessage().contains("không đủ hàng trong kho"));
            assertEquals(10, inventory.getQuantity()); // Stock remains unchanged
        }
    }

    @Test
    void testDeleteSalesOrder_Success() {
        try (MockedStatic<TenantSecurityUtil> mockedSecurity = Mockito.mockStatic(TenantSecurityUtil.class)) {
            mockedSecurity.when(TenantSecurityUtil::getCurrentTenantId).thenReturn(tenantId);

            SalesOrder mockOrder = new SalesOrder();
            mockOrder.setId(UUID.randomUUID());
            mockOrder.setTenant(tenant);

            SalesOrderItem item = new SalesOrderItem();
            item.setVariant(variant);
            item.setQuantity(3);
            mockOrder.setSalesOrderItems(Collections.singletonList(item));

            when(salesOrderRepository.findById(mockOrder.getId())).thenReturn(Optional.of(mockOrder));
            when(inventoryRepository.findByVariantId(variant.getId())).thenReturn(Collections.singletonList(inventory));

            salesOrderService.deleteSalesOrder(mockOrder.getId());

            assertEquals(13, inventory.getQuantity()); // Stock restored: 10 + 3
            verify(salesOrderRepository).delete(mockOrder);
        }
    }
}
