package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record SalesOrderItemResponse(
        UUID id,
        int quantity,
        double unitPrice,
        double discount,
        UUID variantId,
        String sku
) {
}
