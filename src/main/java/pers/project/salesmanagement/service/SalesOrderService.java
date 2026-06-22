package pers.project.salesmanagement.service;

import pers.project.salesmanagement.dto.request.CreateSalesOrderRequest;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;

public interface SalesOrderService {
    SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request);
}
