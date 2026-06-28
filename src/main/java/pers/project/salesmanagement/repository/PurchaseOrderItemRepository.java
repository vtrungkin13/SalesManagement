package pers.project.salesmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.PurchaseOrderItem;

import java.util.UUID;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, UUID> {
}
