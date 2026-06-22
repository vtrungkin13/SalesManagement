package pers.project.salesmanagement.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateSalesOrderRequest(
    UUID customerId,
    List<CreateSalesOrderItemRequest> salesOrderItemRequests
) {
}
