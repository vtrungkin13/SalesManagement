package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreateGoodsReceiptRequest;
import pers.project.salesmanagement.dto.response.GoodsReceiptResponse;

import java.util.UUID;

public interface GoodsReceiptService {
    GoodsReceiptResponse createGoodsReceipt(CreateGoodsReceiptRequest request);
    Page<GoodsReceiptResponse> getGoodsReceipts(Pageable pageable);
    GoodsReceiptResponse getGoodsReceiptById(UUID id);
}
