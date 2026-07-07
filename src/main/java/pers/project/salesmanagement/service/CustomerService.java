package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreateCustomerRequest;
import pers.project.salesmanagement.dto.response.CustomerResponse;

import java.util.UUID;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    Page<CustomerResponse> getCustomers(Pageable pageable);

    CustomerResponse getCustomerById(UUID id);

    CustomerResponse updateCustomer(UUID id, CreateCustomerRequest request);

    void deleteCustomer(UUID id);
}
