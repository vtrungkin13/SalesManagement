package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, columnDefinition = "nvarchar(100)")
    private String name;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String phone;

    @Email
    @Size(max = 100)
    @Column(columnDefinition = "varchar(100)")
    private String email;

    @Size(max = 255)
    @Column(columnDefinition = "nvarchar(255)")
    private String address;

    @Min(0)
    @Column(name = "loyalty_point", nullable = false, columnDefinition = "float check (loyalty_point >= 0)")
    @ColumnDefault("0.0")
    private double loyaltyPoint;

    @Min(0)
    @Column(name = "total_spent", nullable = false, columnDefinition = "float check (total_spent >= 0)")
    @ColumnDefault("0.0")
    private double totalSpent;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    private List<SalesOrder> salesOrders;
}
