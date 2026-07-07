package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateSupplierRequest;
import pers.project.salesmanagement.dto.response.SupplierResponse;
import pers.project.salesmanagement.entity.Supplier;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.mapper.SupplierMapper;
import pers.project.salesmanagement.repository.PurchaseOrderRepository;
import pers.project.salesmanagement.repository.SupplierRepository;
import pers.project.salesmanagement.repository.TenantRepository;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.SupplierService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final TenantRepository tenantRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponse createSupplier(CreateSupplierRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        Supplier supplier = supplierMapper.toEntity(request);

        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        supplier.setTenant(tenant);

        Supplier savedSupplier = supplierRepository.save(supplier);

        return supplierMapper.toResponse(savedSupplier);
    }

    @Override
    public Page<SupplierResponse> getSuppliers(Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        return supplierRepository.findByTenantId(tenantId, pageable)
                .map(supplierMapper::toResponse);
    }

    @Override
    public SupplierResponse getSupplierById(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        Supplier supplier = supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return supplierMapper.toResponse(supplier);
    }

    @Override
    public SupplierResponse updateSupplier(UUID id, CreateSupplierRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        Supplier supplier = supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setCode(request.code());
        supplier.setName(request.name());
        supplier.setPhone(request.phone());
        supplier.setEmail(request.email());
        supplier.setAddress(request.address());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponse(updatedSupplier);
    }

    @Override
    public void deleteSupplier(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        Supplier supplier = supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        if (purchaseOrderRepository.existsBySupplierId(id)) {
            throw new RuntimeException("Cannot delete supplier because they are associated with purchase orders");
        }

        supplierRepository.delete(supplier);
    }
}
