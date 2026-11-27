package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.ProductCategory;

import java.util.UUID;

public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, UUID> {
}