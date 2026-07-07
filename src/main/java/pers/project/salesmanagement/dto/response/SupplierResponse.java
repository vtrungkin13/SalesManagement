package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String code,
        String name,
        String phone,
        String email,
        String address,
        String tenantName
) {
}
