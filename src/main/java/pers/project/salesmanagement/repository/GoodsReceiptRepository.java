package pers.project.salesmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.GoodsReceipt;

import java.util.UUID;

@Repository
public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, UUID> {
    Page<GoodsReceipt> findByTenantId(UUID tenantId, Pageable pageable);
}
