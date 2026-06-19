package pers.project.salesmanagement.dto.request;


import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record CreateAppUserRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String name,
        @NotBlank String phone,
        @NotNull UUID tenantId,
        @NotEmpty List<UUID> rolesId
) {
}
