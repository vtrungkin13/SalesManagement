package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreatePurchaseOrderRequest;
import pers.project.salesmanagement.dto.response.PurchaseOrderResponse;
import pers.project.salesmanagement.entity.*;
import pers.project.salesmanagement.entity.status.PurchaseStatus;
import pers.project.salesmanagement.mapper.PurchaseOrderMapper;
import pers.project.salesmanagement.repository.*;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.PurchaseOrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductVariantRepository productVariantRepository;
    private final TenantRepository tenantRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public PurchaseOrderResponse createPurchaseOrder(CreatePurchaseOrderRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        if (supplier.getTenant() == null || !supplier.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Supplier access denied");
        }

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setTenant(tenant);
        po.setStatus(PurchaseStatus.PENDING);
        po.setPoNumber("PO-" + System.currentTimeMillis());

        List<PurchaseOrderItem> items = new ArrayList<>();
        double amount = 0.0;

        for (var itemReq : request.items()) {
            ProductVariant variant = productVariantRepository.findById(itemReq.variantId())
                    .orElseThrow(() -> new RuntimeException("Product variant not found"));

            if (variant.getTenant() == null || !variant.getTenant().getId().equals(tenantId)) {
                throw new RuntimeException("Product variant access denied");
            }

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setVariant(variant);
            item.setQuantity(itemReq.quantity());
            item.setUnitCost(itemReq.unitCost());
            item.setPurchaseOrder(po);

            items.add(item);
            amount += item.getUnitCost() * item.getQuantity();
        }

        po.setPurchaseOrderItems(items);
        po.setAmount(amount);

        PurchaseOrder savedPo = purchaseOrderRepository.save(po);
        purchaseOrderItemRepository.saveAll(items);

        return purchaseOrderMapper.toResponse(savedPo);
    }

    @Override
    public Page<PurchaseOrderResponse> getPurchaseOrders(Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        return purchaseOrderRepository.findByTenantId(tenantId, pageable)
                .map(purchaseOrderMapper::toResponse);
    }

    @Override
    public PurchaseOrderResponse getPurchaseOrderById(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        if (po.getTenant() == null || !po.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Access denied to this purchase order");
        }

        return purchaseOrderMapper.toResponse(po);
    }
}
