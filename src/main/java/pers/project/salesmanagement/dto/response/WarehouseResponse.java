package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        String code,
        String name,
        String address,
        String tenantName
) {
}
