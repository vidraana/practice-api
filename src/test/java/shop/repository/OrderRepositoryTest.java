package shop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.support.PostgresTestConfiguration;
import shop.ShopApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import shop.model.Order;
import shop.model.OrderDetail;
import shop.model.OrderDetailId;
import shop.model.Product;
import shop.support.TestData;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ShopApplication.class, PostgresTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class OrderRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private OrderRepository orders;
    @Autowired
    private OrderDetailRepository details;
    @Autowired
    private ProductRepository products;
    @Autowired
    private LocationRepository locations;
    @Autowired
    private UserRepository users;

    @Test
    void shouldPersistOrderWithEmbeddedAddressAndGeneratedTimestamp() {
        Order order = new Order(TestData.persistCustomer(em), TestData.address());
        order.addOrderDetail(TestData.persistProduct(em), TestData.persistLocation(em), 1);
        orders.save(order);
        em.flush();
        em.clear();
        Order loaded = orders.findById(order.getId()).orElseThrow();
        assertEquals("Vienna", loaded.getAddress().getCity());
        assertNotNull(loaded.getCreatedAt());
    }

    @Test
    void shouldPreserveSuppliedTimestamp() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 1, 12, 0);
        Order order = new Order(TestData.persistCustomer(em), TestData.address(), timestamp);
        order.addOrderDetail(TestData.persistProduct(em), TestData.persistLocation(em), 1);
        orders.save(order);
        em.flush();
        em.clear();
        assertEquals(timestamp, orders.findById(order.getId()).orElseThrow().getCreatedAt());
    }

    @Test
    void shouldCascadeNewDetailsAndDeriveCompositeKey() {
        var product = TestData.persistProduct(em);
        var location = TestData.persistLocation(em);
        Order order = new Order(TestData.persistCustomer(em), TestData.address());
        order.addOrderDetail(product, location, 2);
        orders.save(order);
        em.flush();
        em.clear();
        Order loaded = orders.findById(order.getId()).orElseThrow();
        assertEquals(1, loaded.getOrderDetails().size());
        OrderDetail detail = details.findById(new OrderDetailId(order.getId(), product.getId())).orElseThrow();
        assertEquals(2, detail.getQuantity());
        assertEquals(location.getId(), detail.getShippedFrom().getId());
        assertEquals(product.getId(), detail.getProduct().getId());
    }

    @Test
    void shouldCascadeOrderDeletionButKeepReferencedEntities() {
        var customer = TestData.persistCustomer(em);
        var product = TestData.persistProduct(em);
        var location = TestData.persistLocation(em);
        Order order = new Order(customer, TestData.address());
        order.addOrderDetail(product, location, 2);
        orders.save(order);
        em.flush();
        em.clear();
        orders.deleteById(order.getId());
        em.flush();
        em.clear();
        assertFalse(orders.existsById(order.getId()));
        assertFalse(details.existsById(new OrderDetailId(order.getId(), product.getId())));
        assertTrue(products.existsById(product.getId()));
        assertTrue(locations.existsById(location.getId()));
        assertTrue(users.existsById(customer.getId()));
    }

    @Test
    void shouldRejectDifferentProductObjectsWithTheSameId() {
        Product first = TestData.persistProduct(em);
        em.flush();
        em.clear();
        Product second = products.findById(first.getId()).orElseThrow();
        assertNotSame(first, second);

        Order order = new Order(TestData.persistCustomer(em), TestData.address());
        var location = TestData.persistLocation(em);
        order.addOrderDetail(first, location, 1);

        assertThrows(IllegalArgumentException.class,
                () -> order.addOrderDetail(second, location, 2));
        assertEquals(1, order.getOrderDetails().size());
    }
}
