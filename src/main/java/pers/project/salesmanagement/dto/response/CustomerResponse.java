package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String phone,
        String email,
        String address,
        double loyaltyPoint,
        double totalSpent,
        String tenantName
) {
}
