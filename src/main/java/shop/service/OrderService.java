package shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.exception.InsufficientStockException;
import shop.exception.ResourceNotFoundException;
import shop.model.*;
import shop.repository.*;
import shop.strategy.OrderStrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderStrategy orderStrategy;

    @Transactional
    public Order createOrder(OrderRequest request) {

        if (request == null || request.userId() == null
                || request.address() == null
                || request.createdAt() == null) {

            throw new IllegalArgumentException(
                    "Customer, address and timestamp are required");
        }

        validateItems(request.items());

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + request.userId()));

        Map<UUID, Product> products = new HashMap<>();

        for (OrderItem item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: " + item.productId()));

            products.put(item.productId(), product);
        }

        List<StockAllocation> allocations =
                orderStrategy.selectLocations(request.items());

        Order order = new Order(
                user,
                request.address(),
                request.createdAt()
        );

        for (StockAllocation allocation : allocations) {

            UUID productId = allocation.product().getId();

            StockId stockId = new StockId(
                    productId,
                    allocation.location().getId()
            );

            Stock stock = stockRepository.findById(stockId)
                    .orElseThrow(() -> new InsufficientStockException(
                            "Stock not found"));

            stock.decreaseQuantity(allocation.quantity());

            order.addOrderDetail(
                    products.get(productId),
                    allocation.location(),
                    allocation.quantity()
            );
        }

        return orderRepository.save(order);
    }

    private void validateItems(List<OrderItem> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one product");
        }

        Set<UUID> productIds = new HashSet<>();

        for (OrderItem item : items) {

            if (item == null) {
                throw new IllegalArgumentException(
                        "Order item cannot be null");
            }

            if (!productIds.add(item.productId())) {
                throw new IllegalArgumentException(
                        "Duplicate product in order");
            }
        }
    }
}