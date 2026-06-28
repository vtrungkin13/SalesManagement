package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreatePurchaseOrderRequest;
import pers.project.salesmanagement.dto.response.PurchaseOrderResponse;

import java.util.UUID;

public interface PurchaseOrderService {
    PurchaseOrderResponse createPurchaseOrder(CreatePurchaseOrderRequest request);
    Page<PurchaseOrderResponse> getPurchaseOrders(Pageable pageable);
    PurchaseOrderResponse getPurchaseOrderById(UUID id);
}
