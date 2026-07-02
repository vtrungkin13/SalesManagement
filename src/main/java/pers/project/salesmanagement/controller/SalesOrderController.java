package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.project.salesmanagement.dto.request.CreateSalesOrderRequest;
import pers.project.salesmanagement.dto.request.UpdateSalesOrderRequest;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import pers.project.salesmanagement.service.SalesOrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/sales-order")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping("/create")
    public ResponseEntity<SalesOrderResponse> createSalesOrder(@RequestBody CreateSalesOrderRequest request) {
        SalesOrderResponse salesOrderResponse = salesOrderService.createSalesOrder(request);
        return new ResponseEntity<>(salesOrderResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<SalesOrderResponse>> getSalesOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SalesOrderResponse> salesOrders = salesOrderService.getSalesOrders(pageable);
        return new ResponseEntity<>(salesOrders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponse> getSalesOrderById(@PathVariable UUID id) {
        SalesOrderResponse salesOrderResponse = salesOrderService.getSalesOrderById(id);
        return new ResponseEntity<>(salesOrderResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesOrderResponse> updateSalesOrder(
            @PathVariable UUID id,
            @RequestBody UpdateSalesOrderRequest request
    ) {
        SalesOrderResponse salesOrderResponse = salesOrderService.updateSalesOrder(id, request);
        return new ResponseEntity<>(salesOrderResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesOrder(@PathVariable UUID id) {
        salesOrderService.deleteSalesOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
