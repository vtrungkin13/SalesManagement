package pers.project.salesmanagement.dto.request;

import java.util.UUID;

public record CreatePurchaseOrderItemRequest(
        UUID variantId,
        int quantity,
        double unitCost
) {
}
