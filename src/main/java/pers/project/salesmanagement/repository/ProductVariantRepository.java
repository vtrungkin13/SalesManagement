package pers.project.salesmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.ProductVariant;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    boolean existsByTenantIdAndSku(UUID tenantId, String sku);

    long countByTenantId(UUID tenantId);

    @Query("SELECT pv.sku FROM ProductVariant pv WHERE pv.tenant.id = :tenantId AND pv.sku IN :skus")
    List<String> findExistingSkus(@Param("tenantId") UUID tenantId, @Param("skus") List<String> skus);
}
