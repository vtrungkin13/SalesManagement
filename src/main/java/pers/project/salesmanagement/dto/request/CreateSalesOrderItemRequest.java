package pers.project.salesmanagement.dto.request;

import java.util.UUID;

public record CreateSalesOrderItemRequest(
        UUID variantId,
        int quantity,
        double discount
) {
}
