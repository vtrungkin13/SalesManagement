package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.project.salesmanagement.dto.response.InventoryResponse;
import pers.project.salesmanagement.dto.response.InventoryStatsResponse;
import pers.project.salesmanagement.service.InventoryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Page<InventoryResponse>> getInventoryLevels(
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false) UUID variantId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "quantity") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InventoryResponse> response = inventoryService.getInventoryLevels(warehouseId, variantId, query, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<InventoryStatsResponse> getInventoryStats(
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(defaultValue = "5") int lowStockThreshold
    ) {
        InventoryStatsResponse response = inventoryService.getInventoryStats(warehouseId, lowStockThreshold);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
