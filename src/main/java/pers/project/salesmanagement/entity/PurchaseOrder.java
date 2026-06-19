package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pers.project.salesmanagement.entity.status.PurchaseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "int default 0")
    private PurchaseStatus status = PurchaseStatus.PENDING;

    private double amount;

    private String poNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "purchaseOrder")
    private List<PurchaseOrderItem> purchaseOrderItems;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "purchaseOrder")
    private List<GoodsReceipt> goodsReceipts;
}
