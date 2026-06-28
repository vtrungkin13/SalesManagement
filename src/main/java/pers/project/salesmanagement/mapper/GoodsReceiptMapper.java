package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.response.GoodsReceiptResponse;
import pers.project.salesmanagement.entity.GoodsReceipt;

@Mapper(componentModel = "spring", uses = {GoodsReceiptItemMapper.class})
public interface GoodsReceiptMapper {

    @Mapping(source = "purchaseOrder.id", target = "purchaseOrderId")
    @Mapping(source = "purchaseOrder.poNumber", target = "poNumber")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "goodsReceiptItems", target = "items")
    GoodsReceiptResponse toResponse(GoodsReceipt gr);
}
