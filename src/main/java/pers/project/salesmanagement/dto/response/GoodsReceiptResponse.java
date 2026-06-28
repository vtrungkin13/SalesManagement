package pers.project.salesmanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GoodsReceiptResponse(
        UUID id,
        String receiptNumber,
        LocalDateTime receiptDate,
        UUID purchaseOrderId,
        String poNumber,
        String warehouseName,
        List<GoodsReceiptItemResponse> items
) {
}
