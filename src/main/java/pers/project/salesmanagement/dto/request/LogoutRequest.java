package pers.project.salesmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
                @NotBlank String refreshToken) {
}
