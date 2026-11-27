package shop.strategy;

import lombok.RequiredArgsConstructor;
import shop.exception.InsufficientStockException;
import shop.model.*;
import shop.repository.LocationRepository;
import shop.repository.StockRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SingleLocationStrategy implements OrderStrategy {

    private final LocationRepository locationRepository;
    private final StockRepository stockRepository;

    @Override
    public List<StockAllocation> selectLocations(
            List<OrderItem> items) {

        List<Location> locations =
                locationRepository.findAllByOrderByIdAsc();

        for (Location location : locations) {

            List<StockAllocation> allocations =
                    findAllocations(location, items);

            if (!allocations.isEmpty()) {
                return allocations;
            }
        }

        throw new InsufficientStockException(
                "No single location can fulfill the order");
    }

    private List<StockAllocation> findAllocations(
            Location location, List<OrderItem> items) {

        List<StockAllocation> allocations = new ArrayList<>();

        for (OrderItem item : items) {

            StockId stockId = new StockId(
                    item.productId(),
                    location.getId()
            );

            Stock stock = stockRepository.findById(stockId)
                    .orElse(null);

            if (stock == null || stock.getQuantity() < item.quantity()) {
                return List.of();
            }

            allocations.add(new StockAllocation(
                    location,
                    stock.getProduct(),
                    item.quantity()
            ));
        }

        return allocations;
    }
}