package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreateSupplierRequest;
import pers.project.salesmanagement.dto.response.SupplierResponse;

import java.util.UUID;

public interface SupplierService {

    SupplierResponse createSupplier(CreateSupplierRequest request);

    Page<SupplierResponse> getSuppliers(Pageable pageable);

    SupplierResponse getSupplierById(UUID id);

    SupplierResponse updateSupplier(UUID id, CreateSupplierRequest request);

    void deleteSupplier(UUID id);
}
