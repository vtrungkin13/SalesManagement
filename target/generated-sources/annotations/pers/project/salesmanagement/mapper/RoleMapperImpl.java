package pers.project.salesmanagement.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.response.RoleResponse;
import pers.project.salesmanagement.entity.Role;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-25T16:24:46+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleResponse toResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String description = null;

        id = role.getId();
        name = role.getName();
        description = role.getDescription();

        RoleResponse roleResponse = new RoleResponse( id, name, description );

        return roleResponse;
    }

    @Override
    public List<RoleResponse> toResponseList(List<Role> roles) {
        if ( roles == null ) {
            return null;
        }

        List<RoleResponse> list = new ArrayList<RoleResponse>( roles.size() );
        for ( Role role : roles ) {
            list.add( toResponse( role ) );
        }

        return list;
    }
}
