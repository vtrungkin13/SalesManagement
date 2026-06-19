package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.salesmanagement.entity.status.ProductStatus;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_variant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID  id;

    private String sku;

    private String barcode;

    @Column(name = "cost_price")
    private double costPrice;

    @Column(name = "sell_price")
    private double sellPrice;

    @Column(columnDefinition = "int default 0")
    private ProductStatus status = ProductStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
    private List<PurchaseOrderItem> purchaseOrderItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant")
    private List<Inventory> inventories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant")
    private List<SalesOrderItem>  salesOrderItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant")
    private List<ReturnOrderItem> returnOrderItems;
}
