package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.project.salesmanagement.dto.request.CreateGoodsReceiptRequest;
import pers.project.salesmanagement.dto.response.GoodsReceiptResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import pers.project.salesmanagement.service.GoodsReceiptService;

import java.util.UUID;

@RestController
@RequestMapping("/api/goods-receipt")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class GoodsReceiptController {

    private final GoodsReceiptService goodsReceiptService;

    @PostMapping
    public ResponseEntity<GoodsReceiptResponse> createGoodsReceipt(@RequestBody CreateGoodsReceiptRequest request) {
        GoodsReceiptResponse response = goodsReceiptService.createGoodsReceipt(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<GoodsReceiptResponse>> getGoodsReceipts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "receiptDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<GoodsReceiptResponse> response = goodsReceiptService.getGoodsReceipts(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceiptResponse> getGoodsReceiptById(@PathVariable UUID id) {
        GoodsReceiptResponse response = goodsReceiptService.getGoodsReceiptById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
