package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateWarehouseRequest;
import pers.project.salesmanagement.dto.response.WarehouseResponse;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.entity.WareHouse;
import pers.project.salesmanagement.mapper.WarehouseMapper;
import pers.project.salesmanagement.repository.GoodsReceiptRepository;
import pers.project.salesmanagement.repository.InventoryRepository;
import pers.project.salesmanagement.repository.TenantRepository;
import pers.project.salesmanagement.repository.WarehouseRepository;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.WarehouseService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final TenantRepository tenantRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseResponse createWarehouse(CreateWarehouseRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        WareHouse warehouse = warehouseMapper.toEntity(request);

        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        warehouse.setTenant(tenant);

        WareHouse savedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toResponse(savedWarehouse);
    }

    @Override
    public Page<WarehouseResponse> getWarehouses(Pageable pageable) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        return warehouseRepository.findByTenantId(tenantId, pageable)
                .map(warehouseMapper::toResponse);
    }

    @Override
    public WarehouseResponse getWarehouseById(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        WareHouse warehouse = warehouseRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    public WarehouseResponse updateWarehouse(UUID id, CreateWarehouseRequest request) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        WareHouse warehouse = warehouseRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        warehouse.setCode(request.code());
        warehouse.setName(request.name());
        warehouse.setAddress(request.address());

        WareHouse updatedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toResponse(updatedWarehouse);
    }

    @Override
    public void deleteWarehouse(UUID id) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }
        WareHouse warehouse = warehouseRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        if (goodsReceiptRepository.existsByWarehouseId(id)) {
            throw new RuntimeException("Cannot delete warehouse because it is associated with goods receipts");
        }

        if (inventoryRepository.existsByWarehouseId(id)) {
            throw new RuntimeException("Cannot delete warehouse because it contains inventory items");
        }

        warehouseRepository.delete(warehouse);
    }
}
