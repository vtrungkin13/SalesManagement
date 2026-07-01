package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WareHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String code;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, columnDefinition = "nvarchar(100)")
    private String name;

    @Size(max = 255)
    @Column(columnDefinition = "nvarchar(255)")
    private String address;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<GoodsReceipt> goodsReceipts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<Inventory> inventories;
}
