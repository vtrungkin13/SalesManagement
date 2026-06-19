package pers.project.salesmanagement.service;

import pers.project.salesmanagement.dto.request.CreateTenantRequest;
import pers.project.salesmanagement.dto.response.TenantResponse;

import java.util.List;

public interface TenantService {
    TenantResponse createTenant(CreateTenantRequest request);

    List<TenantResponse> getAllTenants();
}
