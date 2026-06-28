package pers.project.salesmanagement.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateGoodsReceiptRequest(
        UUID purchaseOrderId,
        UUID warehouseId,
        List<CreateGoodsReceiptItemRequest> items
) {
}
