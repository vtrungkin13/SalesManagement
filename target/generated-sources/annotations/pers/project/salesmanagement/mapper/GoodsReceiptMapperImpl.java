package pers.project.salesmanagement.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.GoodsReceiptItemResponse;
import pers.project.salesmanagement.dto.response.GoodsReceiptResponse;
import pers.project.salesmanagement.entity.GoodsReceipt;
import pers.project.salesmanagement.entity.PurchaseOrder;
import pers.project.salesmanagement.entity.WareHouse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T19:01:44+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class GoodsReceiptMapperImpl implements GoodsReceiptMapper {

    @Autowired
    private GoodsReceiptItemMapper goodsReceiptItemMapper;

    @Override
    public GoodsReceiptResponse toResponse(GoodsReceipt gr) {
        if ( gr == null ) {
            return null;
        }

        UUID purchaseOrderId = null;
        String poNumber = null;
        String warehouseName = null;
        List<GoodsReceiptItemResponse> items = null;
        UUID id = null;
        String receiptNumber = null;
        LocalDateTime receiptDate = null;

        purchaseOrderId = grPurchaseOrderId( gr );
        poNumber = grPurchaseOrderPoNumber( gr );
        warehouseName = grWarehouseName( gr );
        items = goodsReceiptItemMapper.toResponseList( gr.getGoodsReceiptItems() );
        id = gr.getId();
        receiptNumber = gr.getReceiptNumber();
        receiptDate = gr.getReceiptDate();

        GoodsReceiptResponse goodsReceiptResponse = new GoodsReceiptResponse( id, receiptNumber, receiptDate, purchaseOrderId, poNumber, warehouseName, items );

        return goodsReceiptResponse;
    }

    private UUID grPurchaseOrderId(GoodsReceipt goodsReceipt) {
        PurchaseOrder purchaseOrder = goodsReceipt.getPurchaseOrder();
        if ( purchaseOrder == null ) {
            return null;
        }
        return purchaseOrder.getId();
    }

    private String grPurchaseOrderPoNumber(GoodsReceipt goodsReceipt) {
        PurchaseOrder purchaseOrder = goodsReceipt.getPurchaseOrder();
        if ( purchaseOrder == null ) {
            return null;
        }
        return purchaseOrder.getPoNumber();
    }

    private String grWarehouseName(GoodsReceipt goodsReceipt) {
        WareHouse warehouse = goodsReceipt.getWarehouse();
        if ( warehouse == null ) {
            return null;
        }
        return warehouse.getName();
    }
}
