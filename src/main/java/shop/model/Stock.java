package shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@Table(name = "stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @EmbeddedId
    private StockId id = new StockId();

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @MapsId("locationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private int quantity;

    @Version
    @Column(nullable = false)
    private Long version;

    public Stock(Product product, Location location, int quantity) {
        this.product = Objects.requireNonNull(product, "Product is required");
        this.location = Objects.requireNonNull(location, "Location is required");

        if (quantity < 0) {
            throw new IllegalArgumentException(
                    "Initial stock cannot be negative"
            );
        }

        this.quantity = quantity;
    }

    public void decreaseQuantity(int amount) {
        validateAmount(amount);

        if (amount > quantity) {
            throw new IllegalStateException("Insufficient stock");
        }

        quantity -= amount;
    }

    public void increaseQuantity(int amount) {
        validateAmount(amount);

        quantity = Math.addExact(quantity, amount);
    }

    private static void validateAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }
    }
}
