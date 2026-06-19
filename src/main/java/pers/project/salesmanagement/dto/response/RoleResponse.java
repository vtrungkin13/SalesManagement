package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description
) {
}
