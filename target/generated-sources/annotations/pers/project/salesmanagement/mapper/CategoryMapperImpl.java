package pers.project.salesmanagement.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.request.CreateCategoryRequest;
import pers.project.salesmanagement.dto.response.CategoryResponse;
import pers.project.salesmanagement.entity.Category;
import pers.project.salesmanagement.entity.Tenant;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-28T15:58:28+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category toEntity(CreateCategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( request.name() );

        return category;
    }

    @Override
    public CategoryResponse toResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        String tenantName = null;
        UUID id = null;
        String name = null;

        tenantName = categoryTenantName( category );
        id = category.getId();
        name = category.getName();

        CategoryResponse categoryResponse = new CategoryResponse( id, name, tenantName );

        return categoryResponse;
    }

    private String categoryTenantName(Category category) {
        Tenant tenant = category.getTenant();
        if ( tenant == null ) {
            return null;
        }
        return tenant.getName();
    }
}
