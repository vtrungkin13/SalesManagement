package pers.project.salesmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank(message = "Product code cannot be blank")
        String code,

        @NotBlank(message = "Product name cannot be blank")
        String name,

        String description,

        String imageUrl,

        @NotNull(message = "Category ID cannot be null")
        UUID categoryId,

        @NotBlank(message = "SKU cannot be blank")
        String sku,

        @Positive(message = "Sell price must be greater than zero")
        double sellPrice,

        @PositiveOrZero(message = "Cost price must be zero or positive")
        double costPrice
) {
}
