package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.salesmanagement.dto.request.CreateSalesOrderRequest;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;
import pers.project.salesmanagement.service.SalesOrderService;

@RestController
@RequestMapping("/api/sales-order")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping("/create")
    public ResponseEntity<SalesOrderResponse> createSalesOrder(@RequestBody CreateSalesOrderRequest request) {
        SalesOrderResponse salesOrderResponse = salesOrderService.createSalesOrder(request);

        return new ResponseEntity<>(salesOrderResponse, HttpStatus.CREATED);
    }
}
