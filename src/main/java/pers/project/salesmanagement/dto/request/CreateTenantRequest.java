package pers.project.salesmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTenantRequest(
        @NotBlank String code,
        @NotBlank String name
) {
}
