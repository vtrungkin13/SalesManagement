package pers.project.salesmanagement.service;

import pers.project.salesmanagement.dto.response.RoleResponse;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    List<RoleResponse> getAllRoles();

    RoleResponse getRoleByName(String roleName);

    RoleResponse getRoleById(UUID roleId);
}
