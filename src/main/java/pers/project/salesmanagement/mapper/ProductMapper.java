package pers.project.salesmanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pers.project.salesmanagement.dto.request.CreateProductRequest;
import pers.project.salesmanagement.dto.response.ProductDetailResponse;
import pers.project.salesmanagement.dto.response.ProductResponse;
import pers.project.salesmanagement.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(CreateProductRequest request);

    @Mapping(source = "image.imageUrl", target = "imageUrl")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);

    @Mapping(source = "product.image.imageUrl", target = "imageUrl")
    @Mapping(source = "product.category.name", target = "categoryName")
    @Mapping(source = "inventory", target = "currentInventory")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "sku", target = "sku")
    ProductDetailResponse toDetailResponse(Product product, int inventory, double price, String sku);
}
