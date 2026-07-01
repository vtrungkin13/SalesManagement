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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales_order", indexes = {
        @Index(name = "idx_so_tenant", columnList = "tenant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "order_number", nullable = false, unique = true, columnDefinition = "varchar(50)")
    private String orderNumber;

    @Min(0)
    @Column(nullable = false, columnDefinition = "float check (subtotal >= 0)")
    private double subtotal;

    @Min(0)
    @Column(nullable = false, columnDefinition = "float check (discount >= 0)")
    private double discount;

    @Min(0)
    @Column(nullable = false, columnDefinition = "float check (total >= 0)")
    private double total;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private List<SalesOrderItem> salesOrderItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "salesOrder")
    private List<ReturnOrder> returnOrders;

}
