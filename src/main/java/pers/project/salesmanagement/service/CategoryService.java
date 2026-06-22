package pers.project.salesmanagement.service;

import pers.project.salesmanagement.dto.request.CreateCategoryRequest;
import pers.project.salesmanagement.dto.response.CategoryResponse;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);
}
