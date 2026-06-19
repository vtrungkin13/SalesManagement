package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateTenantRequest;
import pers.project.salesmanagement.dto.response.TenantResponse;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.mapper.TenantMapper;
import pers.project.salesmanagement.repository.TenantRepository;
import pers.project.salesmanagement.service.TenantService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public TenantResponse createTenant(CreateTenantRequest request) {
        Tenant tenant = tenantMapper.toEntity(request);
        tenantRepository.save(tenant);

        return tenantMapper.toResponse(tenant);
    }

    @Override
    public List<TenantResponse> getAllTenants() {
        List<Tenant> tenants = tenantRepository.findAll();

        return tenantMapper.toResponseList(tenants);
    }
}
