package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.response.RoleResponse;
import pers.project.salesmanagement.entity.Role;
import pers.project.salesmanagement.mapper.RoleMapper;
import pers.project.salesmanagement.repository.RoleRepository;
import pers.project.salesmanagement.service.RoleService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        return roleMapper.toResponseList(roles);
    }

    @Override
    public RoleResponse getRoleByName(String roleName) {
        Role role = roleRepository.findByName(roleName).orElse(null);

        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse getRoleById(UUID roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);

        return roleMapper.toResponse(role);
    }
}
