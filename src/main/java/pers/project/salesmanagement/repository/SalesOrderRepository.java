package pers.project.salesmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.SalesOrder;

import java.util.UUID;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
}
