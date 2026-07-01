package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "sales_order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Min(1)
    @Column(nullable = false, columnDefinition = "int check (quantity >= 1)")
    private int quantity;

    @Min(0)
    @Column(name = "unit_price", nullable = false, columnDefinition = "float check (unit_price >= 0)")
    private double unitPrice;

    @Min(0)
    @Column(nullable = false, columnDefinition = "float check (discount >= 0)")
    private double discount;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "so_id", nullable = false)
    private SalesOrder salesOrder;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;
}
