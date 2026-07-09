package pers.project.salesmanagement.service;

import org.springframework.web.multipart.MultipartFile;
import pers.project.salesmanagement.dto.response.ImportJobResponse;
import pers.project.salesmanagement.dto.response.ImportJobErrorResponse;

import java.util.List;
import java.util.UUID;

public interface ProductImportService {
    ImportJobResponse startImport(MultipartFile file);
    ImportJobResponse getJobStatus(UUID jobId);
    ImportJobResponse retryImport(UUID jobId);
    List<ImportJobErrorResponse> getJobErrors(UUID jobId);
}
