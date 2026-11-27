package shop.mapper;

import org.springframework.stereotype.Component;
import shop.dto.ProductDTO;
import shop.model.Product;
import shop.model.ProductCategory;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .weight(product.getWeight())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .categoryDescription(product.getCategory().getDescription())
                .build();
    }

    public Product toEntity(ProductDTO dto, ProductCategory category) {
        return new Product(
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getWeight(),
                category,
                dto.getImageUrl()
        );
    }
}