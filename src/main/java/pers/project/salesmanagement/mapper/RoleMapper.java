package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import pers.project.salesmanagement.dto.response.RoleResponse;
import pers.project.salesmanagement.entity.Role;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toResponse(Role role);

    List<RoleResponse> toResponseList(List<Role> roles);
}
