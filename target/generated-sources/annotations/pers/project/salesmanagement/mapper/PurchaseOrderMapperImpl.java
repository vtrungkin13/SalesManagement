package pers.project.salesmanagement.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.PurchaseOrderItemResponse;
import pers.project.salesmanagement.dto.response.PurchaseOrderResponse;
import pers.project.salesmanagement.entity.PurchaseOrder;
import pers.project.salesmanagement.entity.Supplier;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-29T20:28:33+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class PurchaseOrderMapperImpl implements PurchaseOrderMapper {

    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    @Override
    public PurchaseOrderResponse toResponse(PurchaseOrder po) {
        if ( po == null ) {
            return null;
        }

        String supplierName = null;
        List<PurchaseOrderItemResponse> items = null;
        UUID id = null;
        String poNumber = null;
        String status = null;
        double amount = 0.0d;
        LocalDateTime createdAt = null;

        supplierName = poSupplierName( po );
        items = purchaseOrderItemMapper.toResponseList( po.getPurchaseOrderItems() );
        id = po.getId();
        poNumber = po.getPoNumber();
        if ( po.getStatus() != null ) {
            status = po.getStatus().name();
        }
        amount = po.getAmount();
        createdAt = po.getCreatedAt();

        PurchaseOrderResponse purchaseOrderResponse = new PurchaseOrderResponse( id, poNumber, status, amount, createdAt, supplierName, items );

        return purchaseOrderResponse;
    }

    private String poSupplierName(PurchaseOrder purchaseOrder) {
        Supplier supplier = purchaseOrder.getSupplier();
        if ( supplier == null ) {
            return null;
        }
        return supplier.getName();
    }
}
