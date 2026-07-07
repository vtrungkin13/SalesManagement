package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateCustomerRequest;
import pers.project.salesmanagement.dto.response.CustomerResponse;
import pers.project.salesmanagement.entity.Customer;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.mapper.CustomerMapper;
import pers.project.salesmanagement.repository.CustomerRepository;
import pers.project.salesmanagement.repository.SalesOrderRepository;
import pers.project.salesmanagement.repository.TenantRepository;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.CustomerService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final TenantRepository tenantRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        Customer customer = customerMapper.toEntity(request);

        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        customer.setTenant(tenant);

        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public Page<CustomerResponse> getCustomers(Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        return customerRepository.findByTenantId(tenantId, pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    public CustomerResponse getCustomerById(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        Customer customer = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(UUID id, CreateCustomerRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        Customer customer = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        customer.setAddress(request.address());

        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        Customer customer = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (salesOrderRepository.existsByCustomerId(id)) {
            throw new RuntimeException("Cannot delete customer because they are associated with sales orders");
        }

        customerRepository.delete(customer);
    }
}
