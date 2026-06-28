package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record InventoryResponse(
                UUID inventoryId,
                UUID warehouseId,
                String warehouseName,
                UUID productId,
                String productName,
                UUID variantId,
                String sku,
                int quantity,
                double costPrice,
                double sellPrice) {
}
