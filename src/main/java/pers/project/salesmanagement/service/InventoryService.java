package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.response.InventoryResponse;
import pers.project.salesmanagement.dto.response.InventoryStatsResponse;

import java.util.UUID;

public interface InventoryService {

    Page<InventoryResponse> getInventoryLevels(UUID warehouseId, UUID variantId, String query, Pageable pageable);

    InventoryStatsResponse getInventoryStats(UUID warehouseId, int lowStockThreshold);
}
