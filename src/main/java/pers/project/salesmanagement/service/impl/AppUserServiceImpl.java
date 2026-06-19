package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateAppUserRequest;
import pers.project.salesmanagement.dto.request.UpdateAppUserRequest;
import pers.project.salesmanagement.dto.response.AppUserResponse;
import pers.project.salesmanagement.entity.Role;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.mapper.AppUserMapper;
import pers.project.salesmanagement.repository.RoleRepository;
import pers.project.salesmanagement.repository.TenantRepository;
import pers.project.salesmanagement.repository.AppUserRepository;
import pers.project.salesmanagement.service.AppUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUserResponse createUser(CreateAppUserRequest request) {
        if (appUserRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (request.password().length() < 8) {
            throw new RuntimeException("Password too short");
        }

        AppUser appUser = appUserMapper.toEntity(request);

        Tenant tenant = tenantRepository.getReferenceById(request.tenantId());
        appUser.setTenant(tenant);

        List<Role> userRoles = new ArrayList<>();
        request.rolesId().forEach(roleId -> userRoles.add(roleRepository.getReferenceById(roleId)));
        appUser.setRoles(userRoles);

        appUser.setPassword(passwordEncoder.encode(request.password()));

        AppUser savedUser = appUserRepository.save(appUser);

        return appUserMapper.toResponse(savedUser);
    }

    @Override
    public List<AppUserResponse> getAllUsers() {
        List<AppUser> appUsers = appUserRepository.findAll();
        return appUserMapper.toResponseList(appUsers);
    }

    @Override
    public AppUserResponse updateUser(UpdateAppUserRequest request) {
        AppUser appUser = appUserRepository.findById(request.id()).orElseThrow(() -> new RuntimeException("User not found"));

        appUser.setEmail(request.email());
        appUser.setName(request.name());
        appUser.setPhone(request.phone());

        return appUserMapper.toResponse(appUser);
    }
}
