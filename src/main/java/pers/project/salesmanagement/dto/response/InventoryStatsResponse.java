package pers.project.salesmanagement.dto.response;

public record InventoryStatsResponse(
        long totalItems,
        long totalUniqueVariants,
        double totalCostValue,
        double totalSellValue,
        long outOfStockCount,
        long lowStockCount
) {}
