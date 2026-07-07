package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.request.CreateSupplierRequest;
import pers.project.salesmanagement.dto.response.SupplierResponse;
import pers.project.salesmanagement.entity.Supplier;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toEntity(CreateSupplierRequest request);

    @Mapping(source = "tenant.name", target = "tenantName")
    SupplierResponse toResponse(Supplier supplier);
}
