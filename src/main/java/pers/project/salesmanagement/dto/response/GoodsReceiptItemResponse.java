package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record GoodsReceiptItemResponse(
        UUID id,
        UUID variantId,
        String sku,
        int quantity
) {
}
