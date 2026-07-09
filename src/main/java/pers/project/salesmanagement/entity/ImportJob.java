package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pers.project.salesmanagement.entity.status.ImportJobStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "import_job")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @Column(name = "file_path", nullable = false, columnDefinition = "nvarchar(500)")
    private String filePath;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private ImportJobStatus status = ImportJobStatus.PENDING;

    @Column(name = "total_rows", nullable = false)
    private int totalRows = 0;

    @Column(name = "processed_offset", nullable = false)
    private int processedOffset = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
