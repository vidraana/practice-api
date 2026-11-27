package shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.model.Product;
import shop.model.ProductCategory;
import shop.repository.ProductCategoryRepository;
import shop.repository.ProductRepository;

import shop.exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found: " + id));
    }

    public ProductCategory findCategoryById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Category ID is required");
        }

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found: " + id));
    }

    @Transactional
    public Product create(Product product) {
        if (product.getId() != null) {
            throw new IllegalArgumentException(
                    "A new product must not have an ID");
        }

        product.setCategory(
                findCategoryById(product.getCategory().getId())
        );

        return productRepository.save(product);
    }

    @Transactional
    public Product update(UUID id, Product updatedProduct) {
        Product existingProduct = findById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setWeight(updatedProduct.getWeight());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());

        existingProduct.setCategory(
                findCategoryById(updatedProduct.getCategory().getId())
        );

        return existingProduct;
    }

    @Transactional
    public void delete(UUID id) {
        Product product = findById(id);

        productRepository.delete(product);
    }
}