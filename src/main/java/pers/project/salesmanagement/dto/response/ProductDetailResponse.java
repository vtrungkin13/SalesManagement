package pers.project.salesmanagement.dto.response;

import java.util.UUID;

public record ProductDetailResponse(
        UUID id,
        String code,
        String name,
        String description,
        String imageUrl,
        String categoryName,
        int currentInventory,
        double price,
        String sku
) {
}
