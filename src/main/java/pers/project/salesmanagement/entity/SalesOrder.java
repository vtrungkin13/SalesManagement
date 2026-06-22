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
@Table(name = "sales_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_number")
    private String orderNumber;

    private double subtotal;

    private double discount;

    private double total;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private List<SalesOrderItem>  salesOrderItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "salesOrder")
    private List<ReturnOrder> returnOrders;


}
