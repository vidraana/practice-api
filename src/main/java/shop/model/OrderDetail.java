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
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @EmbeddedId
    private OrderDetailId id;

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipped_from", nullable = false)
    private Location shippedFrom;

    @Column(nullable = false)
    private Integer quantity;

    public OrderDetail(Order order, Product product,
                       Location shippedFrom, int quantity) {

        this.order = Objects.requireNonNull(order);
        this.product = Objects.requireNonNull(product);
        this.shippedFrom = Objects.requireNonNull(shippedFrom);

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Order quantity must be positive"
            );
        }

        this.quantity = quantity;

        this.id = new OrderDetailId(
                Objects.requireNonNull(order.getId()),
                Objects.requireNonNull(product.getId())
        );
    }
}