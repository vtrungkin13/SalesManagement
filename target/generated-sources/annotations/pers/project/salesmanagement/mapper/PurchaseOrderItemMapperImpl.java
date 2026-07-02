package pers.project.salesmanagement.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.PurchaseOrderItemResponse;
import pers.project.salesmanagement.entity.ProductVariant;
import pers.project.salesmanagement.entity.PurchaseOrderItem;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T19:01:44+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class PurchaseOrderItemMapperImpl implements PurchaseOrderItemMapper {

    @Override
    public PurchaseOrderItemResponse toResponse(PurchaseOrderItem item) {
        if ( item == null ) {
            return null;
        }

        UUID variantId = null;
        String sku = null;
        UUID id = null;
        int quantity = 0;
        double unitCost = 0.0d;

        variantId = itemVariantId( item );
        sku = itemVariantSku( item );
        id = item.getId();
        quantity = item.getQuantity();
        unitCost = item.getUnitCost();

        PurchaseOrderItemResponse purchaseOrderItemResponse = new PurchaseOrderItemResponse( id, variantId, sku, quantity, unitCost );

        return purchaseOrderItemResponse;
    }

    @Override
    public List<PurchaseOrderItemResponse> toResponseList(List<PurchaseOrderItem> items) {
        if ( items == null ) {
            return null;
        }

        List<PurchaseOrderItemResponse> list = new ArrayList<PurchaseOrderItemResponse>( items.size() );
        for ( PurchaseOrderItem purchaseOrderItem : items ) {
            list.add( toResponse( purchaseOrderItem ) );
        }

        return list;
    }

    private UUID itemVariantId(PurchaseOrderItem purchaseOrderItem) {
        ProductVariant variant = purchaseOrderItem.getVariant();
        if ( variant == null ) {
            return null;
        }
        return variant.getId();
    }

    private String itemVariantSku(PurchaseOrderItem purchaseOrderItem) {
        ProductVariant variant = purchaseOrderItem.getVariant();
        if ( variant == null ) {
            return null;
        }
        return variant.getSku();
    }
}
