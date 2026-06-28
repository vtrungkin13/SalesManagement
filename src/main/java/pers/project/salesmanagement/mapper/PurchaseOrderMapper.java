package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.response.PurchaseOrderResponse;
import pers.project.salesmanagement.entity.PurchaseOrder;

@Mapper(componentModel = "spring", uses = {PurchaseOrderItemMapper.class})
public interface PurchaseOrderMapper {

    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "purchaseOrderItems", target = "items")
    PurchaseOrderResponse toResponse(PurchaseOrder po);
}
