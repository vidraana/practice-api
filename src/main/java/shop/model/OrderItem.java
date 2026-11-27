package shop.model;

import java.util.UUID;

public record OrderItem(UUID productId, int quantity) {

    public OrderItem {
        if (productId == null) {
            throw new IllegalArgumentException(
                    "Product ID is required");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive");
        }
    }
}