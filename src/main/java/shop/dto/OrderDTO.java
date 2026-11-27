package shop.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
        UUID id,
        UUID userId,
        LocalDateTime createdAt,
        AddressDTO address,
        List<Item> items
) {
    public record Item(
            UUID productId,
            String productName,
            UUID locationId,
            String locationName,
            int quantity
    ) {
    }
}