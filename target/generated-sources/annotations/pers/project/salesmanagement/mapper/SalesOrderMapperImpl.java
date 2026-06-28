package pers.project.salesmanagement.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.SalesOrderItemResponse;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;
import pers.project.salesmanagement.entity.Customer;
import pers.project.salesmanagement.entity.SalesOrder;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-28T17:17:18+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class SalesOrderMapperImpl implements SalesOrderMapper {

    @Autowired
    private SalesOrderItemMapper salesOrderItemMapper;

    @Override
    public SalesOrderResponse toResponse(SalesOrder salesOrder) {
        if ( salesOrder == null ) {
            return null;
        }

        String customerName = null;
        List<SalesOrderItemResponse> orderItems = null;
        UUID id = null;
        String orderNumber = null;
        double subtotal = 0.0d;
        double discount = 0.0d;
        double total = 0.0d;
        LocalDateTime createdAt = null;

        customerName = salesOrderCustomerName( salesOrder );
        orderItems = salesOrderItemMapper.toResponseList( salesOrder.getSalesOrderItems() );
        id = salesOrder.getId();
        orderNumber = salesOrder.getOrderNumber();
        subtotal = salesOrder.getSubtotal();
        discount = salesOrder.getDiscount();
        total = salesOrder.getTotal();
        createdAt = salesOrder.getCreatedAt();

        SalesOrderResponse salesOrderResponse = new SalesOrderResponse( id, orderNumber, subtotal, discount, total, createdAt, customerName, orderItems );

        return salesOrderResponse;
    }

    private String salesOrderCustomerName(SalesOrder salesOrder) {
        Customer customer = salesOrder.getCustomer();
        if ( customer == null ) {
            return null;
        }
        return customer.getName();
    }
}
