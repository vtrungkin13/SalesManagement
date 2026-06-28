package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pers.project.salesmanagement.entity.status.TenantStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String code;

    @NotNull
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'ACTIVE'")
    private TenantStatus status = TenantStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    private List<AppUser> users;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    private List<Category> categories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    private List<Supplier> suppliers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    private List<Customer> customers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    private List<WareHouse> warehouses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tenant")
    private List<Notification> notifications;
}
