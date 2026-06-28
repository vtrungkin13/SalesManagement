package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.response.InventoryResponse;
import pers.project.salesmanagement.entity.Inventory;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "id", target = "inventoryId")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "variant.product.id", target = "productId")
    @Mapping(source = "variant.product.name", target = "productName")
    @Mapping(source = "variant.id", target = "variantId")
    @Mapping(source = "variant.sku", target = "sku")
    @Mapping(source = "variant.costPrice", target = "costPrice")
    @Mapping(source = "variant.sellPrice", target = "sellPrice")
    InventoryResponse toResponse(Inventory inventory);
}
