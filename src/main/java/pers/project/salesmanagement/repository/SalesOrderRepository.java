package pers.project.salesmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.SalesOrder;

import java.util.UUID;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    Page<SalesOrder> findByTenantId(UUID tenantId, Pageable pageable);

    boolean existsByCustomerId(UUID customerId);
}

