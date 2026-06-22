package pers.project.salesmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.SalesOrderItem;

import java.util.UUID;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, UUID> {
}
