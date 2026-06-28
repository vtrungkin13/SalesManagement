package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record PurchaseOrderItemResponse(
        UUID id,
        UUID variantId,
        String sku,
        int quantity,
        double unitCost
) {
}
