package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goods_receipt", indexes = {
        @Index(name = "idx_gr_tenant", columnList = "tenant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private String receiptNumber;

    @CreationTimestamp
    @Column(name = "receipt_date", updatable = false)
    private LocalDateTime receiptDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "po_id")
    private PurchaseOrder  purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id")
    private WareHouse warehouse;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "goodsReceipt")
    private List<GoodsReceiptItem> goodsReceiptItems;
}
