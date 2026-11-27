package shop.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CompositeIdTest {

    @Test
    void shouldCompareStockIdsByValue() {
        UUID productId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        StockId first = new StockId(productId, locationId);
        StockId second = new StockId(productId, locationId);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new StockId(productId, UUID.randomUUID()));
    }

    @Test
    void shouldCompareOrderDetailIdsByValue() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        OrderDetailId first = new OrderDetailId(orderId, productId);
        OrderDetailId second = new OrderDetailId(orderId, productId);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new OrderDetailId(orderId, UUID.randomUUID()));
    }
}
