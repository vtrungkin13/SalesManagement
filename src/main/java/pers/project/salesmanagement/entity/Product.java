package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.salesmanagement.entity.status.ProductStatus;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_product_tenant_category", columnList = "tenant_id, category_id"),
        @Index(name = "idx_product_tenant_name", columnList = "tenant_id, name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String code;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'ACTIVE'")
    private ProductStatus status = ProductStatus.ACTIVE;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ProductImage image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @OneToMany(mappedBy = "product")
    private List<ProductVariant> variants;
}
