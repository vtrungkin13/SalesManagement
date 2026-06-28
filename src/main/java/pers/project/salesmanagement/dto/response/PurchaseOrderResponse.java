package pers.project.salesmanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PurchaseOrderResponse(
        UUID id,
        String poNumber,
        String status,
        double amount,
        LocalDateTime createdAt,
        String supplierName,
        List<PurchaseOrderItemResponse> items
) {
}
