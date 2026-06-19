package pers.project.salesmanagement.dto.response;

import pers.project.salesmanagement.entity.status.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AppUserResponse(
        UUID id,
        String email,
        String name,
        String phone,
        UserStatus status,
        LocalDateTime createdAt,
        String tenantName,
        List<String> rolesName
) {
}
