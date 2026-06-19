package pers.project.salesmanagement.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record UpdateAppUserRequest(
        @NotNull UUID id,
        @Email @NotBlank String email,
        @NotBlank String name,
        @NotBlank String phone
) {
}
