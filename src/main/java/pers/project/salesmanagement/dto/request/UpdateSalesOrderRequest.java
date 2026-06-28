package pers.project.salesmanagement.dto.request;

import java.util.List;
import java.util.UUID;

public record UpdateSalesOrderRequest(
                UUID customerId,
                List<CreateSalesOrderItemRequest> salesOrderItemRequests) {
}
