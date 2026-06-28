package pers.project.salesmanagement.dto.request;

import java.util.List;
import java.util.UUID;

public record CreatePurchaseOrderRequest(
        UUID supplierId,
        List<CreatePurchaseOrderItemRequest> items
) {
}
