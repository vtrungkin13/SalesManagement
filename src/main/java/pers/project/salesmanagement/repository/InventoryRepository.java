package pers.project.salesmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

        @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.variant.product.id = :productId")
        int sumQuantityByProductId(@Param("productId") UUID productId);

        @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.variant.id = :variantId")
        int sumQuantityByVariantId(@Param("variantId") UUID variantId);

        List<Inventory> findByVariantId(UUID variantId);

        Optional<Inventory> findByWarehouseIdAndVariantId(UUID warehouseId, UUID variantId);

        @Query("SELECT i FROM Inventory i WHERE i.warehouse.tenant.id = :tenantId " +
                        "AND (:warehouseId IS NULL OR i.warehouse.id = :warehouseId) " +
                        "AND (:variantId IS NULL OR i.variant.id = :variantId) " +
                        "AND (:query IS NULL OR i.variant.sku LIKE %:query% OR i.variant.product.name LIKE %:query%)")
        Page<Inventory> findByTenantAndFilters(
                        @Param("tenantId") UUID tenantId,
                        @Param("warehouseId") UUID warehouseId,
                        @Param("variantId") UUID variantId,
                        @Param("query") String query,
                        Pageable pageable);

        @Query("SELECT i FROM Inventory i WHERE i.warehouse.tenant.id = :tenantId " +
                        "AND (:warehouseId IS NULL OR i.warehouse.id = :warehouseId)")
        List<Inventory> findAllByTenantAndWarehouse(
                        @Param("tenantId") UUID tenantId,
                        @Param("warehouseId") UUID warehouseId);

        @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
        @Query("UPDATE Inventory i SET i.quantity = i.quantity - :quantity WHERE i.id = :id AND i.quantity >= :quantity")
        int deductQuantity(@Param("id") java.util.UUID id, @Param("quantity") int quantity);

        boolean existsByWarehouseId(UUID warehouseId);
}

