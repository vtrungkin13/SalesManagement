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
@Table(name = "return_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "return_number", nullable = false, unique = true, columnDefinition = "varchar(50)")
    private String returnNumber;

    @NotBlank
    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String reason;

    @Min(0)
    @Column(name = "total_refund", nullable = false, columnDefinition = "float check (total_refund >= 0)")
    private double totalRefund;

    @CreationTimestamp
    @Column(name = "return_date", updatable = false)
    private LocalDateTime returnDate;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "so_id", nullable = false)
    private SalesOrder salesOrder;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "returnOrder")
    private List<ReturnOrderItem> returnOrderItems;
}
