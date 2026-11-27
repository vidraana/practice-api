package shop.model;

import org.junit.jupiter.api.Test;
import shop.support.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    @Test
    void shouldAddDetailBeforeOrderHasAnId() {
        Order order = new Order(TestData.customer(), TestData.address());
        Product product = TestData.product();
        OrderDetail detail = order.addOrderDetail(product, TestData.location(), 2);
        assertSame(order, detail.getOrder());
        assertSame(product, detail.getProduct());
        assertEquals(2, detail.getQuantity());
        assertEquals(1, order.getOrderDetails().size());
    }

    @Test
    void shouldRejectDuplicateProduct() {
        Order order = new Order(TestData.customer(), TestData.address());
        Product product = TestData.product();
        Location location = TestData.location();
        order.addOrderDetail(product, location, 1);
        assertThrows(IllegalArgumentException.class,
                () -> order.addOrderDetail(product, location, 2));
        assertEquals(1, order.getOrderDetails().size());
    }

    @Test
    void shouldRejectZeroQuantity() {
        Order order = new Order(TestData.customer(), TestData.address());
        Product product = TestData.product();
        Location location = TestData.location();

        assertThrows(IllegalArgumentException.class,
                () -> order.addOrderDetail(product, location, 0));

        assertTrue(order.getOrderDetails().isEmpty());
    }

    @Test
    void shouldProtectDetailsCollection() {
        Order order = new Order(TestData.customer(), TestData.address());
        order.addOrderDetail(TestData.product(), TestData.location(), 1);

        var details = order.getOrderDetails();

        assertThrows(UnsupportedOperationException.class,
                details::clear);
    }

}
