package pers.project.salesmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.Inventory;

import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.variant.product.id = :productId")
    int sumQuantityByProductId(@Param("productId") UUID productId);
}
