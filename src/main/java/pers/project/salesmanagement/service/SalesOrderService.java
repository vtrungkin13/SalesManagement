package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreateSalesOrderRequest;
import pers.project.salesmanagement.dto.request.UpdateSalesOrderRequest;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;

import java.util.UUID;

public interface SalesOrderService {
    SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request);
    Page<SalesOrderResponse> getSalesOrders(Pageable pageable);
    SalesOrderResponse getSalesOrderById(UUID id);
    SalesOrderResponse updateSalesOrder(UUID id, UpdateSalesOrderRequest request);
    void deleteSalesOrder(UUID id);
}
