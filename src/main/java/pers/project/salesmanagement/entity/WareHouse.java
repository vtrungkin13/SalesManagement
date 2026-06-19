package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
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

    private String code;

    private String name;

    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<GoodsReceipt> goodsReceipts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse")
    private List<Inventory> inventories;
}
