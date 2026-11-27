package shop.strategy;

import lombok.RequiredArgsConstructor;
import shop.exception.InsufficientStockException;
import shop.model.*;
import shop.repository.LocationRepository;
import shop.repository.StockRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MostAbundantStrategy implements OrderStrategy {

    private final LocationRepository locationRepository;
    private final StockRepository stockRepository;

    @Override
    public List<StockAllocation> selectLocations(
            List<OrderItem> items) {

        List<Location> locations =
                locationRepository.findAllByOrderByIdAsc();

        List<StockAllocation> allocations = new ArrayList<>();

        for (OrderItem item : items) {

            Stock bestStock = findBestStock(item, locations);

            allocations.add(new StockAllocation(
                    bestStock.getLocation(),
                    bestStock.getProduct(),
                    item.quantity()
            ));
        }

        return allocations;
    }

    private Stock findBestStock(
            OrderItem item, List<Location> locations) {

        Stock bestStock = null;

        for (Location location : locations) {

            StockId stockId = new StockId(
                    item.productId(),
                    location.getId()
            );

            Stock stock = stockRepository.findById(stockId)
                    .orElse(null);

            if (stock == null || stock.getQuantity() < item.quantity()) {
                continue;
            }

            if (bestStock == null
                    || stock.getQuantity() > bestStock.getQuantity()) {
                bestStock = stock;
            }
        }

        if (bestStock == null) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + item.productId());
        }

        return bestStock;
    }
}