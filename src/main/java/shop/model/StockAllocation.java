package shop.model;

public record StockAllocation(
        Location location,
        Product product,
        int quantity
) {
}