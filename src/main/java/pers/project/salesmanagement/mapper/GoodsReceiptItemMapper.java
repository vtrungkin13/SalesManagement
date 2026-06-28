package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.response.GoodsReceiptItemResponse;
import pers.project.salesmanagement.entity.GoodsReceiptItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoodsReceiptItemMapper {

    @Mapping(source = "variant.id", target = "variantId")
    @Mapping(source = "variant.sku", target = "sku")
    GoodsReceiptItemResponse toResponse(GoodsReceiptItem item);

    List<GoodsReceiptItemResponse> toResponseList(List<GoodsReceiptItem> items);
}
