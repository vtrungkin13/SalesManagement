package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.salesmanagement.entity.status.ProductStatus;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "product_variant", uniqueConstraints = {
        @UniqueConstraint(name = "uc_tenant_sku", columnNames = { "tenant_id", "sku" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String sku;

    @Size(max = 50)
    @Column(columnDefinition = "varchar(50)")
    private String barcode;

    @Min(0)
    @Column(name = "cost_price", nullable = false, columnDefinition = "float check (cost_price >= 0)")
    private double costPrice;

    @Min(0)
    @Column(name = "sell_price", nullable = false, columnDefinition = "float check (sell_price >= 0)")
    private double sellPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    @ColumnDefault("'ACTIVE'")
    private ProductStatus status = ProductStatus.ACTIVE;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
    private List<PurchaseOrderItem> purchaseOrderItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant")
    private List<Inventory> inventories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant")
    private List<SalesOrderItem> salesOrderItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant")
    private List<ReturnOrderItem> returnOrderItems;
}
