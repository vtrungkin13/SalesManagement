package pers.project.salesmanagement.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pers.project.salesmanagement.dto.request.CreateProductRequest;
import pers.project.salesmanagement.dto.response.ProductDetailResponse;
import pers.project.salesmanagement.dto.response.ProductResponse;
import pers.project.salesmanagement.entity.Category;
import pers.project.salesmanagement.entity.Product;
import pers.project.salesmanagement.entity.ProductImage;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-28T17:35:48+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(CreateProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product product = new Product();

        product.setCode( request.code() );
        product.setName( request.name() );
        product.setDescription( request.description() );

        return product;
    }

    @Override
    public ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        String imageUrl = null;
        String categoryName = null;
        UUID id = null;
        String code = null;
        String name = null;
        String description = null;

        imageUrl = productImageImageUrl( product );
        categoryName = productCategoryName( product );
        id = product.getId();
        code = product.getCode();
        name = product.getName();
        description = product.getDescription();

        ProductResponse productResponse = new ProductResponse( id, code, name, description, imageUrl, categoryName );

        return productResponse;
    }

    @Override
    public ProductDetailResponse toDetailResponse(Product product, int inventory, double price, String sku) {
        if ( product == null && sku == null ) {
            return null;
        }

        String imageUrl = null;
        String categoryName = null;
        UUID id = null;
        String code = null;
        String name = null;
        String description = null;
        if ( product != null ) {
            imageUrl = productImageImageUrl( product );
            categoryName = productCategoryName( product );
            id = product.getId();
            code = product.getCode();
            name = product.getName();
            description = product.getDescription();
        }
        int currentInventory = 0;
        currentInventory = inventory;
        double price1 = 0.0d;
        price1 = price;
        String sku1 = null;
        sku1 = sku;

        ProductDetailResponse productDetailResponse = new ProductDetailResponse( id, code, name, description, imageUrl, categoryName, currentInventory, price1, sku1 );

        return productDetailResponse;
    }

    private String productImageImageUrl(Product product) {
        ProductImage image = product.getImage();
        if ( image == null ) {
            return null;
        }
        return image.getImageUrl();
    }

    private String productCategoryName(Product product) {
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getName();
    }
}
