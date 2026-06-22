package pers.project.salesmanagement.dto.request;

import java.util.UUID;

public record CreateCategoryRequest(
        String name,
        UUID tenantId
) {
}
