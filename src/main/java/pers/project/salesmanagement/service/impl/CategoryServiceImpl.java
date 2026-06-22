package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateCategoryRequest;
import pers.project.salesmanagement.dto.response.CategoryResponse;
import pers.project.salesmanagement.entity.Category;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.mapper.CategoryMapper;
import pers.project.salesmanagement.repository.CategoryRepository;
import pers.project.salesmanagement.repository.TenantRepository;
import pers.project.salesmanagement.service.CategoryService;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = categoryMapper.toEntity(request);

        Tenant tenant = tenantRepository.getReferenceById(request.tenantId());
        category.setTenant(tenant);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }
}
