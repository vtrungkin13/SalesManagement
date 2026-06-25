package pers.project.salesmanagement.dto.response;

import pers.project.salesmanagement.entity.status.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String email,
        String name,
        String phone,
        UserStatus status,
        LocalDateTime createdAt,
        List<String> rolesName
) {
}
