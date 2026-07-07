package pers.project.salesmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.WareHouse;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<WareHouse, UUID> {
    Page<WareHouse> findByTenantId(UUID tenantId, Pageable pageable);
    Optional<WareHouse> findByIdAndTenantId(UUID id, UUID tenantId);
}

