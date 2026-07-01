package pers.project.salesmanagement.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.SalesOrderItemResponse;
import pers.project.salesmanagement.entity.SalesOrderItem;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-01T14:03:01+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class SalesOrderItemMapperImpl implements SalesOrderItemMapper {

    @Override
    public List<SalesOrderItemResponse> toResponseList(List<SalesOrderItem> salesOrderItems) {
        if ( salesOrderItems == null ) {
            return null;
        }

        List<SalesOrderItemResponse> list = new ArrayList<SalesOrderItemResponse>( salesOrderItems.size() );
        for ( SalesOrderItem salesOrderItem : salesOrderItems ) {
            list.add( salesOrderItemToSalesOrderItemResponse( salesOrderItem ) );
        }

        return list;
    }

    protected SalesOrderItemResponse salesOrderItemToSalesOrderItemResponse(SalesOrderItem salesOrderItem) {
        if ( salesOrderItem == null ) {
            return null;
        }

        UUID id = null;
        int quantity = 0;
        double unitPrice = 0.0d;
        double discount = 0.0d;

        id = salesOrderItem.getId();
        quantity = salesOrderItem.getQuantity();
        unitPrice = salesOrderItem.getUnitPrice();
        discount = salesOrderItem.getDiscount();

        UUID variantId = null;
        String sku = null;

        SalesOrderItemResponse salesOrderItemResponse = new SalesOrderItemResponse( id, quantity, unitPrice, discount, variantId, sku );

        return salesOrderItemResponse;
    }
}
