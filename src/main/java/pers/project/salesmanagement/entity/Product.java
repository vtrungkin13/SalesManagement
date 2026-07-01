package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
}, uniqueConstraints = {
                @UniqueConstraint(name = "uc_tenant_product_code", columnNames = { "tenant_id", "code" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @NotBlank
        @Size(max = 50)
        @Column(nullable = false, columnDefinition = "varchar(50)")
        private String code;

        @NotBlank
        @Size(max = 150)
        @Column(nullable = false, columnDefinition = "nvarchar(150)")
        private String name;

        @Size(max = 1000)
        @Column(columnDefinition = "nvarchar(1000)")
        private String description;

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(nullable = false, columnDefinition = "varchar(50)")
        @org.hibernate.annotations.ColumnDefault("'ACTIVE'")
        private ProductStatus status = ProductStatus.ACTIVE;

        @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        private ProductImage image;

        @NotNull
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "category_id", nullable = false)
        private Category category;

        @NotNull
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "tenant_id", nullable = false)
        private Tenant tenant;

        @OneToMany(mappedBy = "product")
        private List<ProductVariant> variants;
}
