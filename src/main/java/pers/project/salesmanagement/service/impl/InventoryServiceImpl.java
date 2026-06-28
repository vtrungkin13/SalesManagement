package pers.project.salesmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.salesmanagement.dto.response.InventoryResponse;
import pers.project.salesmanagement.dto.response.InventoryStatsResponse;
import pers.project.salesmanagement.entity.Inventory;
import pers.project.salesmanagement.mapper.InventoryMapper;
import pers.project.salesmanagement.repository.InventoryRepository;
import pers.project.salesmanagement.repository.ProductVariantRepository;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.InventoryService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public Page<InventoryResponse> getInventoryLevels(UUID warehouseId, UUID variantId, String query, Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        Page<Inventory> page = inventoryRepository.findByTenantAndFilters(tenantId, warehouseId, variantId, query, pageable);

        return page.map(inventoryMapper::toResponse);
    }

    @Override
    public InventoryStatsResponse getInventoryStats(UUID warehouseId, int lowStockThreshold) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        List<Inventory> list = inventoryRepository.findAllByTenantAndWarehouse(tenantId, warehouseId);

        long totalItems = list.stream().mapToLong(Inventory::getQuantity).sum();
        long totalUniqueVariants = list.stream()
                .filter(i -> i.getQuantity() > 0)
                .map(i -> i.getVariant().getId())
                .distinct()
                .count();

        double totalCostValue = list.stream()
                .mapToDouble(i -> i.getQuantity() * i.getVariant().getCostPrice())
                .sum();

        double totalSellValue = list.stream()
                .mapToDouble(i -> i.getQuantity() * i.getVariant().getSellPrice())
                .sum();

        long lowStockCount = list.stream()
                .filter(i -> i.getQuantity() > 0 && i.getQuantity() <= lowStockThreshold)
                .map(i -> i.getVariant().getId())
                .distinct()
                .count();

        long totalVariantsCount = productVariantRepository.countByTenantId(tenantId);
        long outOfStockCount = Math.max(0L, totalVariantsCount - totalUniqueVariants);

        return new InventoryStatsResponse(
                totalItems,
                totalUniqueVariants,
                totalCostValue,
                totalSellValue,
                outOfStockCount,
                lowStockCount
        );
    }
}
