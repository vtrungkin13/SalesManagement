package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreateWarehouseRequest;
import pers.project.salesmanagement.dto.response.WarehouseResponse;

import java.util.UUID;

public interface WarehouseService {

    WarehouseResponse createWarehouse(CreateWarehouseRequest request);

    Page<WarehouseResponse> getWarehouses(Pageable pageable);

    WarehouseResponse getWarehouseById(UUID id);

    WarehouseResponse updateWarehouse(UUID id, CreateWarehouseRequest request);

    void deleteWarehouse(UUID id);
}
