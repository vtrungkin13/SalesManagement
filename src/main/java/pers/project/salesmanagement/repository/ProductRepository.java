package pers.project.salesmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.Product;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE p.tenant.id = :tenantId " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> findByTenantAndFilters(
            @Param("tenantId") UUID tenantId,
            @Param("categoryId") UUID categoryId,
            @Param("name") String name,
            Pageable pageable
    );

    boolean existsByCategoryId(UUID categoryId);
}

