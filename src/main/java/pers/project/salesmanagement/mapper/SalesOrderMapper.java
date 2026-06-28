package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;
import pers.project.salesmanagement.entity.SalesOrder;

@Mapper(componentModel = "spring", uses = { SalesOrderItemMapper.class })
public interface SalesOrderMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "salesOrderItems", target = "orderItems")
    SalesOrderResponse toResponse(SalesOrder salesOrder);
}
