package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.request.CreateTenantRequest;
import pers.project.salesmanagement.dto.response.TenantResponse;
import pers.project.salesmanagement.entity.Tenant;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    Tenant toEntity(CreateTenantRequest request);

    @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "createdAt")
    TenantResponse toResponse(Tenant tenant);

    @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "createdAt")
    List<TenantResponse> toResponseList(List<Tenant> tenants);
}
