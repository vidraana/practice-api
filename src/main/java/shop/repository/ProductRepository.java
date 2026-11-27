package shop.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository
        extends JpaRepository<Product, UUID> {

    @Override
    @EntityGraph(attributePaths = "category")
    Optional<Product> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = "category")
    List<Product> findAll();

    @EntityGraph(attributePaths = "category")
    List<Product> findByName(String name);

    @EntityGraph(attributePaths = "category")
    List<Product> findByCategory_Id(UUID categoryId);
}
