package shop.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "stock")
public class Stock {

    @EmbeddedId
    private StockId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @MapsId("locationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Setter
    @Column(nullable = false)
    private Integer quantity;

    public Stock(Product product, Location location, Integer quantity) {
        this.product = Objects.requireNonNull(product);
        this.location = Objects.requireNonNull(location);
        this.quantity = Objects.requireNonNull(quantity);

        this.id = new StockId(
                Objects.requireNonNull(product.getId()),
                Objects.requireNonNull(location.getId())
        );
    }
}