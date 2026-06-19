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
@Table(name = "return_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "return_number")
    private String returnNumer;

    @Column(columnDefinition = "varchar(max)")
    private String reason;

    @Column(name = "total_refund")
    private double totalRefund;

    @CreationTimestamp
    @Column(name = "return_date",  updatable = false)
    private LocalDateTime returnDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "so_id")
    private SalesOrder salesOrder;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "returnOrder")
    private List<ReturnOrderItem> returnOrderItems;
}
