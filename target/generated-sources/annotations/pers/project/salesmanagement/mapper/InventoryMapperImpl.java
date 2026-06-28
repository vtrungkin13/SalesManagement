package pers.project.salesmanagement.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.InventoryResponse;
import pers.project.salesmanagement.entity.Inventory;
import pers.project.salesmanagement.entity.Product;
import pers.project.salesmanagement.entity.ProductVariant;
import pers.project.salesmanagement.entity.WareHouse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-28T17:35:48+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public InventoryResponse toResponse(Inventory inventory) {
        if ( inventory == null ) {
            return null;
        }

        UUID inventoryId = null;
        UUID warehouseId = null;
        String warehouseName = null;
        UUID productId = null;
        String productName = null;
        UUID variantId = null;
        String sku = null;
        double costPrice = 0.0d;
        double sellPrice = 0.0d;
        int quantity = 0;

        inventoryId = inventory.getId();
        warehouseId = inventoryWarehouseId( inventory );
        warehouseName = inventoryWarehouseName( inventory );
        productId = inventoryVariantProductId( inventory );
        productName = inventoryVariantProductName( inventory );
        variantId = inventoryVariantId( inventory );
        sku = inventoryVariantSku( inventory );
        costPrice = inventoryVariantCostPrice( inventory );
        sellPrice = inventoryVariantSellPrice( inventory );
        quantity = inventory.getQuantity();

        InventoryResponse inventoryResponse = new InventoryResponse( inventoryId, warehouseId, warehouseName, productId, productName, variantId, sku, quantity, costPrice, sellPrice );

        return inventoryResponse;
    }

    private UUID inventoryWarehouseId(Inventory inventory) {
        WareHouse warehouse = inventory.getWarehouse();
        if ( warehouse == null ) {
            return null;
        }
        return warehouse.getId();
    }

    private String inventoryWarehouseName(Inventory inventory) {
        WareHouse warehouse = inventory.getWarehouse();
        if ( warehouse == null ) {
            return null;
        }
        return warehouse.getName();
    }

    private UUID inventoryVariantProductId(Inventory inventory) {
        ProductVariant variant = inventory.getVariant();
        if ( variant == null ) {
            return null;
        }
        Product product = variant.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getId();
    }

    private String inventoryVariantProductName(Inventory inventory) {
        ProductVariant variant = inventory.getVariant();
        if ( variant == null ) {
            return null;
        }
        Product product = variant.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getName();
    }

    private UUID inventoryVariantId(Inventory inventory) {
        ProductVariant variant = inventory.getVariant();
        if ( variant == null ) {
            return null;
        }
        return variant.getId();
    }

    private String inventoryVariantSku(Inventory inventory) {
        ProductVariant variant = inventory.getVariant();
        if ( variant == null ) {
            return null;
        }
        return variant.getSku();
    }

    private double inventoryVariantCostPrice(Inventory inventory) {
        ProductVariant variant = inventory.getVariant();
        if ( variant == null ) {
            return 0.0d;
        }
        return variant.getCostPrice();
    }

    private double inventoryVariantSellPrice(Inventory inventory) {
        ProductVariant variant = inventory.getVariant();
        if ( variant == null ) {
            return 0.0d;
        }
        return variant.getSellPrice();
    }
}
