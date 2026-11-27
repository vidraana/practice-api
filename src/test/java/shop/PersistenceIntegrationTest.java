package shop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shop.model.*;
import shop.repository.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PersistenceIntegrationTest {

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistAndRetrieveOnlineShopData() {

        // 1. Create category
        ProductCategory category = new ProductCategory();
        category.setName("Electronics");
        category.setDescription("Electronic devices");

        category = categoryRepository.save(category);

        // 2. Create product
        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setWeight(2.5);
        product.setCategory(category);
        product.setImageUrl("https://example.com/laptop.jpg");

        product = productRepository.save(product);

        // 3. Create location
        Location location = new Location();
        location.setName("Warehouse Vienna");
        location.setAddress(new Address(
                "Austria",
                "Vienna",
                "Vienna",
                "Example Street 10"
        ));

        location = locationRepository.save(location);

        // 4. Create stock
        Stock stock = new Stock(product, location, 10);
        stock = stockRepository.save(stock);

        // 5. Create customer
        String suffix = UUID.randomUUID().toString();

        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setUsername("test-" + suffix);
        user.setPassword("test-only-placeholder");
        user.setEmailAddress(suffix + "@example.com");
        user.setRole(UserRole.CUSTOMER);

        user = userRepository.save(user);

        // 6. Create order
        Order order = new Order();
        order.setUser(user);
        order.setAddress(new Address(
                "Austria",
                "Vienna",
                "Vienna",
                "Example Street 25"
        ));

        order = orderRepository.save(order);

        // 7. Create order detail
        OrderDetail detail = new OrderDetail(
                order, product, location, 2
        );

        orderDetailRepository.save(detail);

        // 8. Update stock
        stock.setQuantity(stock.getQuantity() - 2);
        stockRepository.save(stock);

        // 9. Synchronize with database
        entityManager.flush();
        entityManager.clear();

        // 10. Retrieve stored records
        Product savedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        Stock savedStock = stockRepository
                .findById(stock.getId())
                .orElseThrow();

        Order savedOrder = orderRepository
                .findById(order.getId())
                .orElseThrow();

        // 11. Verify results
        assertEquals("Laptop", savedProduct.getName());

        assertEquals(
                new BigDecimal("999.99"),
                savedProduct.getPrice()
        );

        assertEquals(
                "Electronics",
                savedProduct.getCategory().getName()
        );

        assertEquals(8, savedStock.getQuantity());

        assertEquals(
                user.getUsername(),
                savedOrder.getUser().getUsername()
        );

        assertNotNull(savedOrder.getCreatedAt());

        assertEquals(
                "Vienna",
                savedOrder.getAddress().getCity()
        );

        assertEquals(
                1,
                savedOrder.getOrderDetails().size()
        );

        OrderDetail savedDetail =
                savedOrder.getOrderDetails().getFirst();

        assertEquals(2, savedDetail.getQuantity());

        assertEquals(
                location.getId(),
                savedDetail.getShippedFrom().getId()
        );

        assertEquals(
                product.getId(),
                savedDetail.getProduct().getId()
        );

        // 12. Delete dependent records
        orderDetailRepository.deleteById(savedDetail.getId());
        stockRepository.deleteById(savedStock.getId());

        entityManager.flush();
        entityManager.clear();

        // 13. Delete remaining entities
        orderRepository.deleteById(savedOrder.getId());
        productRepository.deleteById(savedProduct.getId());

        entityManager.flush();

        categoryRepository.deleteById(category.getId());
        locationRepository.deleteById(location.getId());
        userRepository.deleteById(user.getId());

        entityManager.flush();
        entityManager.clear();

        // 14. Verify deletion
        assertFalse(orderDetailRepository.existsById(savedDetail.getId()));
        assertFalse(stockRepository.existsById(savedStock.getId()));
        assertFalse(orderRepository.existsById(savedOrder.getId()));
        assertFalse(productRepository.existsById(savedProduct.getId()));
        assertFalse(categoryRepository.existsById(category.getId()));
        assertFalse(locationRepository.existsById(location.getId()));
        assertFalse(userRepository.existsById(user.getId()));
    }
}