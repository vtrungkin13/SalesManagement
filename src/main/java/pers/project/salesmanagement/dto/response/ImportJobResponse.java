package pers.project.salesmanagement.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ImportJobResponse(
        UUID id,
        String status,
        int totalRows,
        int processedOffset,
        double progressPercentage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
