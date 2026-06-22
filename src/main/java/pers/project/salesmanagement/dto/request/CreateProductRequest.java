package pers.project.salesmanagement.dto.request;

import java.util.UUID;

public record CreateProductRequest(
        String code,
        String name,
        String description,
        String imageUrl,
        UUID categoryId
) {
}
