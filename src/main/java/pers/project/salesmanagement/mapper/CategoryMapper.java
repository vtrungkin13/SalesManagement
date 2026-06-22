package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.request.CreateCategoryRequest;
import pers.project.salesmanagement.dto.response.CategoryResponse;
import pers.project.salesmanagement.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CreateCategoryRequest request);

    @Mapping(source = "tenant.name", target = "tenantName")
    CategoryResponse toResponse(Category category);
}
