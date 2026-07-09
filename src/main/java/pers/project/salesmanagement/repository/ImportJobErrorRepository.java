package pers.project.salesmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.project.salesmanagement.entity.ImportJobError;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImportJobErrorRepository extends JpaRepository<ImportJobError, Long> {
    List<ImportJobError> findByJobId(UUID jobId);
}
