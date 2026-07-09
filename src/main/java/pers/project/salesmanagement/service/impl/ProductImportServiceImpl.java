package pers.project.salesmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pers.project.salesmanagement.dto.request.CreateProductRequest;
import pers.project.salesmanagement.dto.response.ImportJobErrorResponse;
import pers.project.salesmanagement.dto.response.ImportJobResponse;
import pers.project.salesmanagement.entity.*;
import pers.project.salesmanagement.entity.status.ImportJobStatus;
import pers.project.salesmanagement.entity.status.ProductStatus;
import pers.project.salesmanagement.mapper.ProductMapper;
import pers.project.salesmanagement.repository.*;
import pers.project.salesmanagement.security.TenantSecurityUtil;
import pers.project.salesmanagement.service.ProductImportService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImportServiceImpl implements ProductImportService {

    private final ImportJobRepository importJobRepository;
    private final ImportJobErrorRepository importJobErrorRepository;
    private final TenantRepository tenantRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMapper productMapper;

    @Autowired
    @Lazy
    private ProductImportServiceImpl self;

    private static final int CHUNK_SIZE = 1000;
    private static final String TEMP_DIR = "temp-imports";

    @Override
    @Transactional
    public ImportJobResponse startImport(MultipartFile file) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant Context not found");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        UUID jobId = UUID.randomUUID();
        try {
            Files.createDirectories(Paths.get(TEMP_DIR));
            Path tempFilePath = Paths.get(TEMP_DIR, jobId + ".csv");
            Files.copy(file.getInputStream(), tempFilePath);

            ImportJob job = new ImportJob();
            job.setId(jobId);
            job.setTenant(tenant);
            job.setFilePath(tempFilePath.toString());
            job.setStatus(ImportJobStatus.PENDING);
            job.setTotalRows(countRows(tempFilePath.toString()) - 1); // exclude header
            job.setProcessedOffset(0);

            ImportJob savedJob = importJobRepository.save(job);

            // Trigger Async Processing
            self.runImportJobAsync(savedJob.getId(), tenantId);

            return mapToResponse(savedJob);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store import file", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ImportJobResponse getJobStatus(UUID jobId) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        ImportJob job = importJobRepository.findByIdAndTenantId(jobId, tenantId)
                .orElseThrow(() -> new RuntimeException("Import Job not found"));
        return mapToResponse(job);
    }

    @Override
    @Transactional
    public ImportJobResponse retryImport(UUID jobId) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        ImportJob job = importJobRepository.findByIdAndTenantId(jobId, tenantId)
                .orElseThrow(() -> new RuntimeException("Import Job not found"));

        if (job.getStatus() != ImportJobStatus.FAILED) {
            throw new RuntimeException("Only failed jobs can be retried");
        }

        job.setStatus(ImportJobStatus.PENDING);
        ImportJob savedJob = importJobRepository.save(job);

        // Trigger Async Processing again
        self.runImportJobAsync(savedJob.getId(), tenantId);

        return mapToResponse(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImportJobErrorResponse> getJobErrors(UUID jobId) {
        UUID tenantId = TenantSecurityUtil.getCurrentTenantId();
        // verify ownership
        importJobRepository.findByIdAndTenantId(jobId, tenantId)
                .orElseThrow(() -> new RuntimeException("Import Job not found"));

        return importJobErrorRepository.findByJobId(jobId).stream()
                .map(err -> new ImportJobErrorResponse(err.getId(), err.getRowNumber(), err.getErrorMessage(), err.getRawData()))
                .collect(Collectors.toList());
    }

    @Async
    public void runImportJobAsync(UUID jobId, UUID tenantId) {
        ImportJob job = importJobRepository.findById(jobId).orElse(null);
        if (job == null) return;

        job.setStatus(ImportJobStatus.PROCESSING);
        importJobRepository.save(job);

        try (BufferedReader br = new BufferedReader(new FileReader(job.getFilePath()))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                job.setStatus(ImportJobStatus.COMPLETED);
                importJobRepository.save(job);
                return;
            }

            int currentOffset = job.getProcessedOffset();
            // Skip rows up to offset
            for (int i = 0; i < currentOffset; i++) {
                br.readLine();
            }

            List<CreateProductRequest> chunk = new ArrayList<>();
            String line;
            int rowCounter = currentOffset + 1; // 1-based indexing for rows (excluding header)

            while ((line = br.readLine()) != null) {
                CreateProductRequest request = parseLineToRequest(line);
                if (request != null) {
                    chunk.add(request);
                }

                if (chunk.size() >= CHUNK_SIZE) {
                    processAndCommitChunk(chunk, tenantId, rowCounter - chunk.size() + 1, job);
                    chunk.clear();
                    currentOffset = rowCounter;
                    job.setProcessedOffset(currentOffset);
                    importJobRepository.save(job);
                }
                rowCounter++;
            }

            // Process remainder
            if (!chunk.isEmpty()) {
                processAndCommitChunk(chunk, tenantId, rowCounter - chunk.size(), job);
                currentOffset = rowCounter - 1;
                job.setProcessedOffset(currentOffset);
            }

            job.setStatus(ImportJobStatus.COMPLETED);
            importJobRepository.save(job);

        } catch (Exception e) {
            job.setStatus(ImportJobStatus.FAILED);
            importJobRepository.save(job);
        }
    }

    private void processAndCommitChunk(List<CreateProductRequest> requests, UUID tenantId, int startRowNumber, ImportJob job) {
        try {
            self.processChunk(requests, tenantId);
        } catch (Exception ex) {
            // Rollback happened, process row-by-row
            self.processRowByRow(requests, tenantId, startRowNumber, job);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processChunk(List<CreateProductRequest> requests, UUID tenantId) {
        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        for (CreateProductRequest request : requests) {
            saveProductRecord(request, tenantId, tenant);
        }
    }

    public void processRowByRow(List<CreateProductRequest> requests, UUID tenantId, int startRowNumber, ImportJob job) {
        Tenant tenant = tenantRepository.getReferenceById(tenantId);
        for (int i = 0; i < requests.size(); i++) {
            CreateProductRequest request = requests.get(i);
            int currentRow = startRowNumber + i;
            try {
                self.saveProductRow(request, tenantId, tenant);
            } catch (Exception ex) {
                ImportJobError error = new ImportJobError();
                error.setJob(job);
                error.setRowNumber(currentRow);
                error.setErrorMessage(ex.getMessage());
                error.setRawData(request.toString());
                importJobErrorRepository.save(error);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductRow(CreateProductRequest request, UUID tenantId, Tenant tenant) {
        saveProductRecord(request, tenantId, tenant);
    }

    private void saveProductRecord(CreateProductRequest request, UUID tenantId, Tenant tenant) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (category.getTenant() == null || !category.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Category does not belong to the current tenant");
        }

        if (request.sellPrice() <= 0) {
            throw new RuntimeException("Price must be greater than zero");
        }

        if (productVariantRepository.existsByTenantIdAndSku(tenantId, request.sku())) {
            throw new RuntimeException("SKU already exists for this tenant");
        }

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setTenant(tenant);
        product.setStatus(ProductStatus.ACTIVE);

        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(request.imageUrl());
            productImage.setProduct(product);
            product.setImage(productImage);
        }

        ProductVariant variant = new ProductVariant();
        variant.setSku(request.sku());
        variant.setCostPrice(request.costPrice());
        variant.setSellPrice(request.sellPrice());
        variant.setProduct(product);
        variant.setTenant(tenant);
        variant.setStatus(ProductStatus.ACTIVE);

        List<ProductVariant> variants = new ArrayList<>();
        variants.add(variant);
        product.setVariants(variants);

        productRepository.save(product);
        productVariantRepository.save(variant);
    }

    private int countRows(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int lines = 0;
            while (br.readLine() != null) lines++;
            return lines;
        }
    }

    private CreateProductRequest parseLineToRequest(String line) {
        List<String> values = parseCsvLine(line);
        if (values.size() < 8) return null;

        try {
            String code = values.get(0);
            String name = values.get(1);
            String description = values.get(2);
            String imageUrl = values.get(3);
            UUID categoryId = UUID.fromString(values.get(4));
            String sku = values.get(5);
            double sellPrice = Double.parseDouble(values.get(6));
            double costPrice = Double.parseDouble(values.get(7));

            return new CreateProductRequest(code, name, description, imageUrl, categoryId, sku, sellPrice, costPrice);
        } catch (Exception e) {
            // Bad formatting line, skip/ignore here, it will be handled by the row-by-row error logger if inside a failed chunk
            return null;
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString().trim());
        return values;
    }

    private ImportJobResponse mapToResponse(ImportJob job) {
        double percentage = job.getTotalRows() > 0 
                ? (double) job.getProcessedOffset() / job.getTotalRows() * 100 
                : 0.0;
        return new ImportJobResponse(
                job.getId(),
                job.getStatus().name(),
                job.getTotalRows(),
                job.getProcessedOffset(),
                Math.round(percentage * 100.0) / 100.0,
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
