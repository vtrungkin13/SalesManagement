package pers.project.salesmanagement.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.GoodsReceiptItemResponse;
import pers.project.salesmanagement.entity.GoodsReceiptItem;
import pers.project.salesmanagement.entity.ProductVariant;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T19:01:44+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class GoodsReceiptItemMapperImpl implements GoodsReceiptItemMapper {

    @Override
    public GoodsReceiptItemResponse toResponse(GoodsReceiptItem item) {
        if ( item == null ) {
            return null;
        }

        UUID variantId = null;
        String sku = null;
        UUID id = null;
        int quantity = 0;

        variantId = itemVariantId( item );
        sku = itemVariantSku( item );
        id = item.getId();
        quantity = item.getQuantity();

        GoodsReceiptItemResponse goodsReceiptItemResponse = new GoodsReceiptItemResponse( id, variantId, sku, quantity );

        return goodsReceiptItemResponse;
    }

    @Override
    public List<GoodsReceiptItemResponse> toResponseList(List<GoodsReceiptItem> items) {
        if ( items == null ) {
            return null;
        }

        List<GoodsReceiptItemResponse> list = new ArrayList<GoodsReceiptItemResponse>( items.size() );
        for ( GoodsReceiptItem goodsReceiptItem : items ) {
            list.add( toResponse( goodsReceiptItem ) );
        }

        return list;
    }

    private UUID itemVariantId(GoodsReceiptItem goodsReceiptItem) {
        ProductVariant variant = goodsReceiptItem.getVariant();
        if ( variant == null ) {
            return null;
        }
        return variant.getId();
    }

    private String itemVariantSku(GoodsReceiptItem goodsReceiptItem) {
        ProductVariant variant = goodsReceiptItem.getVariant();
        if ( variant == null ) {
            return null;
        }
        return variant.getSku();
    }
}
