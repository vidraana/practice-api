package shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    public Product(String name, String description, BigDecimal price,
                   Double weight, ProductCategory category, String imageUrl) {
        setName(name);
        setDescription(description);
        setPrice(price);
        setWeight(weight);
        setCategory(category);
        setImageUrl(imageUrl);
    }

    public void setName(String name) {
        this.name = ModelValidation.requiredText(name, 150, "Product name");
    }

    public void setDescription(String description) {
        this.description = ModelValidation.optionalText(description, 1000, "Product description");
    }

    public void setPrice(BigDecimal price) {
        Objects.requireNonNull(price, "Price is required");
        if (price.signum() < 0 || price.compareTo(new BigDecimal("9999999999.99")) > 0) {
            throw new IllegalArgumentException("Price must be between 0 and 9999999999.99");
        }
        // Reject extra decimal places rather than silently rounding money.
        BigDecimal normalized = price.stripTrailingZeros();
        if (normalized.scale() > 2) {
            throw new IllegalArgumentException("Price must have at most two decimal places");
        }
        this.price = normalized.setScale(2, RoundingMode.UNNECESSARY);
    }

    public void setWeight(Double weight) {
        if (weight == null || !Double.isFinite(weight) || weight <= 0) {
            throw new IllegalArgumentException("Weight must be finite and positive");
        }
        this.weight = weight;
    }

    public void setCategory(ProductCategory category) {
        this.category = Objects.requireNonNull(category, "Category is required");
    }

    public void setImageUrl(String imageUrl) {
        // Stored as text only; validate allowed schemes before displaying or fetching it.
        this.imageUrl = ModelValidation.optionalText(imageUrl, 2048, "Image URL");
    }
}
