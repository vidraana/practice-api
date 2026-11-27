package shop.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderRequest(
        UUID userId,
        LocalDateTime createdAt,
        Address address,
        List<OrderItem> items
) {
}