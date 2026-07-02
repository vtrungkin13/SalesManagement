package pers.project.salesmanagement.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.request.CreateTenantRequest;
import pers.project.salesmanagement.dto.response.TenantResponse;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.entity.status.TenantStatus;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T19:01:44+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class TenantMapperImpl implements TenantMapper {

    @Override
    public Tenant toEntity(CreateTenantRequest request) {
        if ( request == null ) {
            return null;
        }

        Tenant tenant = new Tenant();

        tenant.setCode( request.code() );
        tenant.setName( request.name() );

        return tenant;
    }

    @Override
    public TenantResponse toResponse(Tenant tenant) {
        if ( tenant == null ) {
            return null;
        }

        UUID id = null;
        String code = null;
        String name = null;
        TenantStatus status = null;

        id = tenant.getId();
        code = tenant.getCode();
        name = tenant.getName();
        status = tenant.getStatus();

        LocalDateTime createdAt = java.time.LocalDateTime.now();

        TenantResponse tenantResponse = new TenantResponse( id, code, name, status, createdAt );

        return tenantResponse;
    }

    @Override
    public List<TenantResponse> toResponseList(List<Tenant> tenants) {
        if ( tenants == null ) {
            return null;
        }

        List<TenantResponse> list = new ArrayList<TenantResponse>( tenants.size() );
        for ( Tenant tenant : tenants ) {
            list.add( toResponse( tenant ) );
        }

        return list;
    }
}
