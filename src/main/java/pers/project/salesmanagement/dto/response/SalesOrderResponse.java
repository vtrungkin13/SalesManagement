package pers.project.salesmanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SalesOrderResponse(
        UUID id,
        String orderNumber,
        double subtotal,
        double discount,
        double total,
        LocalDateTime createdAt,
        String customerName,
        List<SalesOrderItemResponse> orderItems
) {
}
