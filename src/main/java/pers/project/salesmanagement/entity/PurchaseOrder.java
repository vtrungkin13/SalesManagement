package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pers.project.salesmanagement.entity.status.PurchaseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_order", indexes = {
        @Index(name = "idx_po_tenant", columnList = "tenant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    @org.hibernate.annotations.ColumnDefault("'PENDING'")
    private PurchaseStatus status = PurchaseStatus.PENDING;

    @Min(0)
    @Column(nullable = false, columnDefinition = "float check (amount >= 0)")
    private double amount;

    @NotBlank
    @Size(max = 50)
    @Column(name = "po_number", nullable = false, unique = true, columnDefinition = "varchar(50)")
    private String poNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "purchaseOrder")
    private List<PurchaseOrderItem> purchaseOrderItems;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "purchaseOrder")
    private List<GoodsReceipt> goodsReceipts;
}
