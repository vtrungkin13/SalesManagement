package pers.project.salesmanagement.dto.response;

import pers.project.salesmanagement.entity.status.TenantStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String code,
        String name,
        TenantStatus status,
        LocalDateTime createdAt
) {
}
