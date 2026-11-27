package shop.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "ShopOrder")
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Embedded
    private Address address;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public Order(User user, Address address) {
        this(user, address, null);
    }

    /** The training model uses LocalDateTime; supplied timestamps must be in UTC. */
    public Order(User user, Address address, LocalDateTime createdAt) {
        this.user = Objects.requireNonNull(user, "Customer is required");
        this.address = Objects.requireNonNull(address, "Delivery address is required");
        this.createdAt = createdAt;
    }

    public List<OrderDetail> getOrderDetails() {
        return Collections.unmodifiableList(orderDetails);
    }

    public OrderDetail addOrderDetail(Product product, Location shippedFrom, int quantity) {
        Objects.requireNonNull(product, "Product is required");
        for (OrderDetail existingDetail : orderDetails) {
            Product existingProduct = existingDetail.getProduct();
            // New products may not have an ID yet, so also check object identity.
            boolean sameProduct = existingProduct == product;
            boolean sameId = product.getId() != null
                    && product.getId().equals(existingProduct.getId());
            if (sameProduct || sameId) {
                throw new IllegalArgumentException("A product may appear only once in an order");
            }
        }
        OrderDetail detail = new OrderDetail(this, product, shippedFrom, quantity);
        orderDetails.add(detail);
        return detail;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(ZoneOffset.UTC);
        }
    }
}
