package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.response.SalesOrderItemResponse;
import pers.project.salesmanagement.entity.SalesOrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalesOrderItemMapper {

    @Mapping(source = "variant.id", target = "variantId")
    @Mapping(source = "variant.sku", target = "sku")
    List<SalesOrderItemResponse> toResponseList(List<SalesOrderItem> salesOrderItems);
}
