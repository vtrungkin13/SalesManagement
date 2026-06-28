package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.request.CreateAppUserRequest;
import pers.project.salesmanagement.dto.request.UpdateAppUserRequest;
import pers.project.salesmanagement.dto.response.AppUserResponse;
import pers.project.salesmanagement.entity.Role;
import pers.project.salesmanagement.entity.AppUser;

import java.util.List;

import pers.project.salesmanagement.dto.response.RegisterResponse;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUser toEntity(CreateAppUserRequest request);

    @Mapping(source = "roles", target = "rolesName")
    RegisterResponse toRegisterResponse(AppUser appUser);

    @Mapping(source = "tenant.name", target = "tenantName")
    @Mapping(source = "roles", target = "rolesName")
    @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "createdAt")
    AppUserResponse toResponse(AppUser appUser);

    @Mapping(source = "tenant.name", target = "tenantName")
    @Mapping(source = "roles", target = "rolesName")
    @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "createdAt")
    List<AppUserResponse> toResponseList(List<AppUser> appUsers);

    AppUser toEntity(UpdateAppUserRequest request);

    default String mapRoleName(Role role) {
        return role.getName();
    }
}
