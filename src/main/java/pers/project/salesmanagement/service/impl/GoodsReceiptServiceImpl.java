package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateGoodsReceiptRequest;
import pers.project.salesmanagement.dto.response.GoodsReceiptResponse;
import pers.project.salesmanagement.entity.*;
import pers.project.salesmanagement.entity.status.PurchaseStatus;
import pers.project.salesmanagement.entity.status.TransactionType;
import pers.project.salesmanagement.mapper.GoodsReceiptMapper;
import pers.project.salesmanagement.repository.*;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.GoodsReceiptService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsReceiptServiceImpl implements GoodsReceiptService {

    private final GoodsReceiptRepository goodsReceiptRepository;
    private final GoodsReceiptItemRepository goodsReceiptItemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final TenantRepository tenantRepository;
    private final GoodsReceiptMapper goodsReceiptMapper;

    @Override
    public GoodsReceiptResponse createGoodsReceipt(CreateGoodsReceiptRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        PurchaseOrder po = purchaseOrderRepository.findById(request.purchaseOrderId())
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (po.getTenant() == null || !po.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Purchase Order access denied");
        }

        WareHouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        if (warehouse.getTenant() == null || !warehouse.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Warehouse access denied");
        }

        // Set status to RECEIVED
        po.setStatus(PurchaseStatus.RECEIVED);
        purchaseOrderRepository.save(po);

        UUID grId = UUID.randomUUID();
        GoodsReceipt gr = new GoodsReceipt();
        gr.setId(grId);
        gr.setPurchaseOrder(po);
        gr.setWarehouse(warehouse);
        gr.setTenant(tenant);
        gr.setReceiptNumber("GR-" + System.currentTimeMillis());

        List<GoodsReceiptItem> items = new ArrayList<>();

        for (var itemReq : request.items()) {
            ProductVariant variant = productVariantRepository.findById(itemReq.variantId())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));

            if (variant.getTenant() == null || !variant.getTenant().getId().equals(tenantId)) {
                throw new RuntimeException("Product variant access denied");
            }

            // Find or Create Inventory record
            Optional<Inventory> invOpt = inventoryRepository.findByWarehouseIdAndVariantId(request.warehouseId(),
                    itemReq.variantId());
            Inventory inventory;
            if (invOpt.isPresent()) {
                inventory = invOpt.get();
                inventory.setQuantity(inventory.getQuantity() + itemReq.quantity());
            } else {
                inventory = new Inventory();
                inventory.setWarehouse(warehouse);
                inventory.setVariant(variant);
                inventory.setQuantity(itemReq.quantity());
            }
            Inventory savedInventory = inventoryRepository.save(inventory);

            // Log Inventory Transaction
            InventoryTransaction tx = new InventoryTransaction();
            tx.setTransactionType(TransactionType.IN);
            tx.setQuantity(itemReq.quantity());
            tx.setInventory(savedInventory);
            tx.setReferenceId(grId);
            inventoryTransactionRepository.save(tx);

            GoodsReceiptItem item = new GoodsReceiptItem();
            item.setGoodsReceipt(gr);
            item.setVariant(variant);
            item.setQuantity(itemReq.quantity());

            items.add(item);
        }

        gr.setGoodsReceiptItems(items);
        GoodsReceipt savedGr = goodsReceiptRepository.save(gr);
        goodsReceiptItemRepository.saveAll(items);

        return goodsReceiptMapper.toResponse(savedGr);
    }

    @Override
    public Page<GoodsReceiptResponse> getGoodsReceipts(Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        return goodsReceiptRepository.findByTenantId(tenantId, pageable)
                .map(goodsReceiptMapper::toResponse);
    }

    @Override
    public GoodsReceiptResponse getGoodsReceiptById(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        GoodsReceipt gr = goodsReceiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goods receipt not found"));

        if (gr.getTenant() == null || !gr.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Access denied to this goods receipt");
        }

        return goodsReceiptMapper.toResponse(gr);
    }
}
