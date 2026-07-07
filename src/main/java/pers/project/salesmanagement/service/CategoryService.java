package pers.project.salesmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pers.project.salesmanagement.dto.request.CreateCategoryRequest;
import pers.project.salesmanagement.dto.response.CategoryResponse;

import java.util.UUID;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    Page<CategoryResponse> getCategories(Pageable pageable);

    CategoryResponse getCategoryById(UUID id);

    CategoryResponse updateCategory(UUID id, CreateCategoryRequest request);

    void deleteCategory(UUID id);
}

