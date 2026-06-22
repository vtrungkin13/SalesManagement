package pers.project.salesmanagement.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record AssignRolesRequest(
        @NotNull UUID id,
        @NotEmpty List<UUID> rolesId
) {
}
