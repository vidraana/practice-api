package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository
        extends JpaRepository<Product, UUID> {

    List<Product> findByName(String name);

    List<Product> findByCategory_Id(UUID categoryId);
}