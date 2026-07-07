package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.request.CreateCustomerRequest;
import pers.project.salesmanagement.dto.response.CustomerResponse;
import pers.project.salesmanagement.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(CreateCustomerRequest request);

    @Mapping(source = "tenant.name", target = "tenantName")
    CustomerResponse toResponse(Customer customer);
}
