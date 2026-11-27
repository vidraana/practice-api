package shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dto.ProductDTO;
import shop.mapper.ProductMapper;
import shop.model.Product;
import shop.model.ProductCategory;
import shop.service.ProductService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public List<ProductDTO> findAll() {
        return productService.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductDTO findById(@PathVariable UUID id) {
        return productMapper.toDTO(
                productService.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(
            @Valid @RequestBody ProductDTO dto) {

        ProductCategory category =
                productService.findCategoryById(dto.getCategoryId());

        Product product = productMapper.toEntity(dto, category);

        Product savedProduct = productService.create(product);

        ProductDTO response = productMapper.toDTO(savedProduct);

        URI location = URI.create("/api/products/" + savedProduct.getId());

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductDTO dto) {

        ProductCategory category =
                productService.findCategoryById(dto.getCategoryId());

        Product product = productMapper.toEntity(dto, category);

        Product updatedProduct = productService.update(id, product);

        return ResponseEntity.ok(
                productMapper.toDTO(updatedProduct)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);

        return ResponseEntity.noContent().build();
    }
}