package pers.project.salesmanagement.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.SalesOrderItemResponse;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;
import pers.project.salesmanagement.entity.Customer;
import pers.project.salesmanagement.entity.SalesOrder;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-28T16:28:11+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class SalesOrderMapperImpl implements SalesOrderMapper {

    @Override
    public SalesOrderResponse toResponse(SalesOrder salesOrder) {
        if ( salesOrder == null ) {
            return null;
        }

        String customerName = null;
        UUID id = null;
        String orderNumber = null;
        double subtotal = 0.0d;
        double discount = 0.0d;
        double total = 0.0d;
        LocalDateTime createdAt = null;

        customerName = salesOrderCustomerName( salesOrder );
        id = salesOrder.getId();
        orderNumber = salesOrder.getOrderNumber();
        subtotal = salesOrder.getSubtotal();
        discount = salesOrder.getDiscount();
        total = salesOrder.getTotal();
        createdAt = salesOrder.getCreatedAt();

        List<SalesOrderItemResponse> orderItems = null;

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
