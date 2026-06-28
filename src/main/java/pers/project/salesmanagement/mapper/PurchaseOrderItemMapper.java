package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.response.PurchaseOrderItemResponse;
import pers.project.salesmanagement.entity.PurchaseOrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseOrderItemMapper {

    @Mapping(source = "variant.id", target = "variantId")
    @Mapping(source = "variant.sku", target = "sku")
    PurchaseOrderItemResponse toResponse(PurchaseOrderItem item);

    List<PurchaseOrderItemResponse> toResponseList(List<PurchaseOrderItem> items);
}
