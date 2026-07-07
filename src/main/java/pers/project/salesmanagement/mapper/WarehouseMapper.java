package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.request.CreateWarehouseRequest;
import pers.project.salesmanagement.dto.response.WarehouseResponse;
import pers.project.salesmanagement.entity.WareHouse;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WareHouse toEntity(CreateWarehouseRequest request);

    @Mapping(source = "tenant.name", target = "tenantName")
    WarehouseResponse toResponse(WareHouse warehouse);
}
