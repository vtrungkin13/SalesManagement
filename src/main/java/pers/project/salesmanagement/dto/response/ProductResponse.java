package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String code,
        String name,
        String description,
        String imageUrl,
        String categoryName
) {
}
