package shop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import shop.ShopApplication;
import shop.exception.InsufficientStockException;
import shop.model.*;
import shop.repository.OrderRepository;
import shop.repository.OrderDetailRepository;
import shop.repository.StockRepository;
import shop.support.PostgresTestConfiguration;
import shop.support.TestData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {
                ShopApplication.class,
                PostgresTestConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "shop.order.strategy=single-location"
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager em;

    private UUID userId;
    private UUID laptopId;
    private UUID keyboardId;
    private UUID locationId;

    @BeforeEach
    void setUp() {
        TransactionTemplate transaction =
                new TransactionTemplate(transactionManager);

        transaction.executeWithoutResult(status -> {
            User user = TestData.persistCustomer(em);

            Product laptop = TestData.persistProduct(em);

            Product keyboard = TestData.persistProduct(em);
            keyboard.setName("Keyboard");

            Location location = TestData.persistLocation(em);

            em.persist(new Stock(laptop, location, 10));
            em.persist(new Stock(keyboard, location, 5));

            em.flush();

            userId = user.getId();
            laptopId = laptop.getId();
            keyboardId = keyboard.getId();
            locationId = location.getId();
        });
    }

    @Test
    void shouldCreateOrderAndDecreaseStock() {
        OrderRequest request = createRequest(2, 3);

        long ordersBefore = orderRepository.count();

        Order order = orderService.createOrder(request);

        assertNotNull(order.getId());

        assertEquals(ordersBefore + 1,
                orderRepository.count());

        Order savedOrder = orderRepository
                .findById(order.getId())
                .orElseThrow();

        assertEquals("Vienna",
                savedOrder.getAddress().getCity());

        OrderDetail laptopDetail = orderDetailRepository
                .findById(new OrderDetailId(order.getId(), laptopId))
                .orElseThrow();

        OrderDetail keyboardDetail = orderDetailRepository
                .findById(new OrderDetailId(order.getId(), keyboardId))
                .orElseThrow();

        assertEquals(2, laptopDetail.getQuantity());
        assertEquals(3, keyboardDetail.getQuantity());

        assertEquals(locationId,
                laptopDetail.getShippedFrom().getId());

        assertEquals(locationId,
                keyboardDetail.getShippedFrom().getId());

        assertEquals(8, getStockQuantity(laptopId));
        assertEquals(2, getStockQuantity(keyboardId));
    }

    @Test
    void shouldRejectOrderAndKeepStockUnchanged() {
        OrderRequest request = createRequest(2, 100);

        long ordersBefore = orderRepository.count();
        long detailsBefore = orderDetailRepository.count();

        assertThrows(InsufficientStockException.class,
                () -> orderService.createOrder(request));

        assertEquals(ordersBefore, orderRepository.count());
        assertEquals(detailsBefore, orderDetailRepository.count());

        assertEquals(10, getStockQuantity(laptopId));
        assertEquals(5, getStockQuantity(keyboardId));
    }

    private OrderRequest createRequest(
            int laptopQuantity, int keyboardQuantity) {

        return new OrderRequest(
                userId,
                LocalDateTime.of(2026, 1, 1, 12, 0),
                TestData.address(),
                List.of(
                        new OrderItem(laptopId, laptopQuantity),
                        new OrderItem(keyboardId, keyboardQuantity)
                )
        );
    }

    private int getStockQuantity(UUID productId) {
        return stockRepository
                .findById(new StockId(productId, locationId))
                .orElseThrow()
                .getQuantity();
    }
}