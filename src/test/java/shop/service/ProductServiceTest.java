package shop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.exception.ResourceNotFoundException;
import shop.model.Product;
import shop.repository.ProductCategoryRepository;
import shop.repository.ProductRepository;
import shop.support.TestData;
import shop.model.ProductCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldFindAllProducts() {
        Product product = TestData.product();

        when(productRepository.findAll())
                .thenReturn(List.of(product));

        List<Product> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals("Laptop", result.getFirst().getName());

        verify(productRepository).findAll();
    }

    @Test
    void shouldFindProductById() {
        UUID id = UUID.randomUUID();
        Product product = TestData.product();

        when(productRepository.findById(id))
                .thenReturn(Optional.of(product));

        Product result = productService.findById(id);

        assertSame(product, result);

        verify(productRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.findById(id));

        verify(productRepository).findById(id);
    }

    @Test
    void shouldCreateProduct() {
        UUID categoryId = UUID.randomUUID();

        ProductCategory category = mock(ProductCategory.class);
        when(category.getId()).thenReturn(categoryId);

        Product product = TestData.product();
        product.setCategory(category);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.save(product))
                .thenReturn(product);

        Product result = productService.create(product);

        assertSame(product, result);

        verify(categoryRepository).findById(categoryId);
        verify(productRepository).save(product);
    }

    @Test
    void shouldUpdateProduct() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductCategory category = mock(ProductCategory.class);
        when(category.getId()).thenReturn(categoryId);

        Product existingProduct = TestData.product();

        Product updatedProduct = TestData.product();
        updatedProduct.setName("Gaming Laptop");
        updatedProduct.setPrice(new BigDecimal("899.99"));
        updatedProduct.setCategory(category);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        Product result = productService.update(
                productId, updatedProduct);

        assertSame(existingProduct, result);
        assertEquals("Gaming Laptop", result.getName());
        assertEquals(new BigDecimal("899.99"), result.getPrice());
        assertSame(category, result.getCategory());

        verify(productRepository).findById(productId);
    }

    @Test
    void shouldDeleteProduct() {
        UUID id = UUID.randomUUID();
        Product product = TestData.product();

        when(productRepository.findById(id))
                .thenReturn(Optional.of(product));

        productService.delete(id);

        verify(productRepository).findById(id);
        verify(productRepository).delete(product);
    }

    @Test
    void shouldRejectProductWithUnknownCategory() {
        UUID categoryId = UUID.randomUUID();

        ProductCategory category = mock(ProductCategory.class);
        when(category.getId()).thenReturn(categoryId);

        Product product = TestData.product();
        product.setCategory(category);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.create(product));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldRejectDeletingUnknownProduct() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.delete(id));

        verify(productRepository, never()).delete(any(Product.class));
    }
}