package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "varchar(max)")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    private List<AppUser> user;
}
