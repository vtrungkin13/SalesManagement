package pers.project.salesmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "import_job_error")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJobError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private ImportJob job;

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "error_message", columnDefinition = "nvarchar(1000)")
    private String errorMessage;

    @Column(name = "raw_data", columnDefinition = "nvarchar(max)")
    private String rawData;
}
