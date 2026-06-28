package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateSalesOrderRequest;
import pers.project.salesmanagement.dto.request.UpdateSalesOrderRequest;
import pers.project.salesmanagement.dto.response.SalesOrderItemResponse;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;
import pers.project.salesmanagement.entity.*;
import pers.project.salesmanagement.entity.status.TransactionType;
import pers.project.salesmanagement.mapper.SalesOrderItemMapper;
import pers.project.salesmanagement.mapper.SalesOrderMapper;
import pers.project.salesmanagement.repository.*;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.SalesOrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductVariantRepository productVariantRepository;
    private final TenantRepository tenantRepository;
    private final InventoryRepository inventoryRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderItemMapper salesOrderItemMapper;

    @Override
    public SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (customer.getTenant() == null || !customer.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Customer does not belong to the current tenant");
        }

        UUID salesOrderId = UUID.randomUUID();
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(salesOrderId);
        salesOrder.setCustomer(customer);
        salesOrder.setTenant(tenant);
        salesOrder.setOrderNumber("SO-" + System.currentTimeMillis());

        List<SalesOrderItem> salesOrderItems = new ArrayList<>();
        double subtotal = 0.0;
        double discount = 0.0;

        for (var itemReq : request.salesOrderItemRequests()) {
            ProductVariant variant = productVariantRepository.findById(itemReq.variantId())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));

            if (variant.getTenant() == null || !variant.getTenant().getId().equals(tenantId)) {
                throw new RuntimeException("Product variant access denied");
            }

            // 1. Verify Stock quantity
            int stock = inventoryRepository.sumQuantityByVariantId(itemReq.variantId());
            if (stock < itemReq.quantity()) {
                throw new RuntimeException("Sản phẩm SKU " + variant.getSku() + " không đủ hàng trong kho. Trong kho: " + stock + ", Yêu cầu: " + itemReq.quantity());
            }

            // 2. Deduct Stock sequentially across warehouses
            int remainingToDeduct = itemReq.quantity();
            List<Inventory> inventories = inventoryRepository.findByVariantId(itemReq.variantId());
            for (Inventory inventory : inventories) {
                if (remainingToDeduct <= 0) break;
                int currentQty = inventory.getQuantity();
                if (currentQty > 0) {
                    int deduct = Math.min(currentQty, remainingToDeduct);
                    inventory.setQuantity(currentQty - deduct);
                    remainingToDeduct -= deduct;
                    inventoryRepository.save(inventory);

                    // Create Inventory Transaction
                    InventoryTransaction tx = new InventoryTransaction();
                    tx.setTransactionType(TransactionType.OUT);
                    tx.setQuantity(deduct);
                    tx.setInventory(inventory);
                    tx.setReferenceId(salesOrderId);
                    inventoryTransactionRepository.save(tx);
                }
            }

            SalesOrderItem salesOrderItem = new SalesOrderItem();
            salesOrderItem.setVariant(variant);
            salesOrderItem.setUnitPrice(variant.getSellPrice());
            salesOrderItem.setQuantity(itemReq.quantity());
            salesOrderItem.setDiscount(itemReq.discount());
            salesOrderItem.setSalesOrder(salesOrder);

            salesOrderItems.add(salesOrderItem);
            subtotal += salesOrderItem.getUnitPrice() * salesOrderItem.getQuantity();
            discount += salesOrderItem.getDiscount();
        }

        salesOrder.setSalesOrderItems(salesOrderItems);
        salesOrder.setSubtotal(subtotal);
        salesOrder.setDiscount(discount);
        salesOrder.setTotal(Math.max(0.0, subtotal - discount));

        SalesOrder savedSalesOrder = salesOrderRepository.save(salesOrder);

        SalesOrderResponse response = salesOrderMapper.toResponse(savedSalesOrder);
        List<SalesOrderItemResponse> itemResponses = salesOrderItemMapper.toResponseList(savedSalesOrder.getSalesOrderItems());
        
        List<SalesOrderItemResponse> responseItems = new ArrayList<>(itemResponses);
        return new SalesOrderResponse(
                response.id(),
                response.orderNumber(),
                response.subtotal(),
                response.discount(),
                response.total(),
                response.createdAt(),
                response.customerName(),
                responseItems
        );
    }

    @Override
    public Page<SalesOrderResponse> getSalesOrders(Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        return salesOrderRepository.findByTenantId(tenantId, pageable)
                .map(order -> {
                    SalesOrderResponse response = salesOrderMapper.toResponse(order);
                    List<SalesOrderItemResponse> itemResponses = salesOrderItemMapper.toResponseList(order.getSalesOrderItems());
                    return new SalesOrderResponse(
                            response.id(),
                            response.orderNumber(),
                            response.subtotal(),
                            response.discount(),
                            response.total(),
                            response.createdAt(),
                            response.customerName(),
                            new ArrayList<>(itemResponses)
                    );
                });
    }

    @Override
    public SalesOrderResponse getSalesOrderById(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getTenant() == null || !order.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Access denied to this order");
        }

        SalesOrderResponse response = salesOrderMapper.toResponse(order);
        List<SalesOrderItemResponse> itemResponses = salesOrderItemMapper.toResponseList(order.getSalesOrderItems());
        return new SalesOrderResponse(
                response.id(),
                response.orderNumber(),
                response.subtotal(),
                response.discount(),
                response.total(),
                response.createdAt(),
                response.customerName(),
                new ArrayList<>(itemResponses)
        );
    }

    @Override
    public SalesOrderResponse updateSalesOrder(UUID id, UpdateSalesOrderRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getTenant() == null || !order.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Access denied to this order");
        }

        // 1. Revert previous stock deductions
        for (SalesOrderItem item : order.getSalesOrderItems()) {
            List<Inventory> inventories = inventoryRepository.findByVariantId(item.getVariant().getId());
            if (!inventories.isEmpty()) {
                Inventory firstInv = inventories.get(0);
                firstInv.setQuantity(firstInv.getQuantity() + item.getQuantity());
                inventoryRepository.save(firstInv);
            }
        }

        // Delete old transactions
        inventoryTransactionRepository.deleteByReferenceId(id);

        // Delete old items
        salesOrderItemRepository.deleteAll(order.getSalesOrderItems());
        order.getSalesOrderItems().clear();

        // 2. Validate and map customer
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.getTenant() == null || !customer.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Customer does not belong to the current tenant");
        }
        order.setCustomer(customer);

        // 3. Process new items with stock check and deduction
        List<SalesOrderItem> newItems = new ArrayList<>();
        double subtotal = 0.0;
        double discount = 0.0;

        for (var itemReq : request.salesOrderItemRequests()) {
            ProductVariant variant = productVariantRepository.findById(itemReq.variantId())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));

            if (variant.getTenant() == null || !variant.getTenant().getId().equals(tenantId)) {
                throw new RuntimeException("Product variant access denied");
            }

            int stock = inventoryRepository.sumQuantityByVariantId(itemReq.variantId());
            if (stock < itemReq.quantity()) {
                throw new RuntimeException("Sản phẩm SKU " + variant.getSku() + " không đủ hàng trong kho. Trong kho: " + stock + ", Yêu cầu: " + itemReq.quantity());
            }

            int remainingToDeduct = itemReq.quantity();
            List<Inventory> inventories = inventoryRepository.findByVariantId(itemReq.variantId());
            for (Inventory inventory : inventories) {
                if (remainingToDeduct <= 0) break;
                int currentQty = inventory.getQuantity();
                if (currentQty > 0) {
                    int deduct = Math.min(currentQty, remainingToDeduct);
                    inventory.setQuantity(currentQty - deduct);
                    remainingToDeduct -= deduct;
                    inventoryRepository.save(inventory);

                    // Create Inventory Transaction
                    InventoryTransaction tx = new InventoryTransaction();
                    tx.setTransactionType(TransactionType.OUT);
                    tx.setQuantity(deduct);
                    tx.setInventory(inventory);
                    tx.setReferenceId(id);
                    inventoryTransactionRepository.save(tx);
                }
            }

            SalesOrderItem salesOrderItem = new SalesOrderItem();
            salesOrderItem.setVariant(variant);
            salesOrderItem.setUnitPrice(variant.getSellPrice());
            salesOrderItem.setQuantity(itemReq.quantity());
            salesOrderItem.setDiscount(itemReq.discount());
            salesOrderItem.setSalesOrder(order);

            newItems.add(salesOrderItem);
            subtotal += salesOrderItem.getUnitPrice() * salesOrderItem.getQuantity();
            discount += salesOrderItem.getDiscount();
        }

        order.getSalesOrderItems().addAll(newItems);
        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTotal(Math.max(0.0, subtotal - discount));

        SalesOrder updatedOrder = salesOrderRepository.save(order);

        SalesOrderResponse response = salesOrderMapper.toResponse(updatedOrder);
        List<SalesOrderItemResponse> itemResponses = salesOrderItemMapper.toResponseList(updatedOrder.getSalesOrderItems());
        return new SalesOrderResponse(
                response.id(),
                response.orderNumber(),
                response.subtotal(),
                response.discount(),
                response.total(),
                response.createdAt(),
                response.customerName(),
                new ArrayList<>(itemResponses)
        );
    }

    @Override
    public void deleteSalesOrder(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getTenant() == null || !order.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Access denied to this order");
        }

        // Restore stock
        for (SalesOrderItem item : order.getSalesOrderItems()) {
            List<Inventory> inventories = inventoryRepository.findByVariantId(item.getVariant().getId());
            if (!inventories.isEmpty()) {
                Inventory firstInv = inventories.get(0);
                firstInv.setQuantity(firstInv.getQuantity() + item.getQuantity());
                inventoryRepository.save(firstInv);
            }
        }

        // Delete related transactions
        inventoryTransactionRepository.deleteByReferenceId(id);

        salesOrderRepository.delete(order);
    }
}
