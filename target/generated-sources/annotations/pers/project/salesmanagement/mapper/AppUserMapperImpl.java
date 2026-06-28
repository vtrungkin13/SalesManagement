package pers.project.salesmanagement.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.request.CreateAppUserRequest;
import pers.project.salesmanagement.dto.request.UpdateAppUserRequest;
import pers.project.salesmanagement.dto.response.AppUserResponse;
import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.entity.Role;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.entity.status.UserStatus;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-28T16:28:12+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class AppUserMapperImpl implements AppUserMapper {

    @Override
    public AppUser toEntity(CreateAppUserRequest request) {
        if ( request == null ) {
            return null;
        }

        AppUser appUser = new AppUser();

        appUser.setEmail( request.email() );
        appUser.setPassword( request.password() );
        appUser.setName( request.name() );
        appUser.setPhone( request.phone() );

        return appUser;
    }

    @Override
    public AppUserResponse toResponse(AppUser appUser) {
        if ( appUser == null ) {
            return null;
        }

        String tenantName = null;
        List<String> rolesName = null;
        UUID id = null;
        String email = null;
        String name = null;
        String phone = null;
        UserStatus status = null;

        tenantName = appUserTenantName( appUser );
        rolesName = roleListToStringList( appUser.getRoles() );
        id = appUser.getId();
        email = appUser.getEmail();
        name = appUser.getName();
        phone = appUser.getPhone();
        status = appUser.getStatus();

        LocalDateTime createdAt = java.time.LocalDateTime.now();

        AppUserResponse appUserResponse = new AppUserResponse( id, email, name, phone, status, createdAt, tenantName, rolesName );

        return appUserResponse;
    }

    @Override
    public List<AppUserResponse> toResponseList(List<AppUser> appUsers) {
        if ( appUsers == null ) {
            return null;
        }

        List<AppUserResponse> list = new ArrayList<AppUserResponse>( appUsers.size() );
        for ( AppUser appUser : appUsers ) {
            list.add( toResponse( appUser ) );
        }

        return list;
    }

    @Override
    public AppUser toEntity(UpdateAppUserRequest request) {
        if ( request == null ) {
            return null;
        }

        AppUser appUser = new AppUser();

        appUser.setId( request.id() );
        appUser.setEmail( request.email() );
        appUser.setName( request.name() );
        appUser.setPhone( request.phone() );

        return appUser;
    }

    private String appUserTenantName(AppUser appUser) {
        Tenant tenant = appUser.getTenant();
        if ( tenant == null ) {
            return null;
        }
        return tenant.getName();
    }

    protected List<String> roleListToStringList(List<Role> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( Role role : list ) {
            list1.add( mapRoleName( role ) );
        }

        return list1;
    }
}
