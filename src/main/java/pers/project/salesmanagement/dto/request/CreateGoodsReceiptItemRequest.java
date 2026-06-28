package pers.project.salesmanagement.dto.request;

import java.util.UUID;

public record CreateGoodsReceiptItemRequest(
        UUID variantId,
        int quantity
) {
}
