package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.project.salesmanagement.dto.request.CreatePurchaseOrderRequest;
import pers.project.salesmanagement.dto.response.PurchaseOrderResponse;
import pers.project.salesmanagement.service.PurchaseOrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(@RequestBody CreatePurchaseOrderRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseOrderResponse>> getPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PurchaseOrderResponse> response = purchaseOrderService.getPurchaseOrders(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrderById(@PathVariable UUID id) {
        PurchaseOrderResponse response = purchaseOrderService.getPurchaseOrderById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
