package shop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shop.support.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StockTest {

    private Product product;
    private Location location;

    @BeforeEach
    void setUp() {
        product = TestData.product();
        location = TestData.location();
    }

    @Test
    void shouldCreateStockWithValidQuantity() {
        Stock stock = new Stock(product, location, 10);

        assertEquals(10, stock.getQuantity());
    }

    @Test
    void shouldRejectNegativeInitialQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Stock(product, location, -1)
        );
    }

    @Test
    void shouldDecreaseQuantity() {
        Stock stock = new Stock(product, location, 10);

        stock.decreaseQuantity(3);

        assertEquals(7, stock.getQuantity());
    }

    @Test
    void shouldIncreaseQuantity() {
        Stock stock = new Stock(product, location, 10);

        stock.increaseQuantity(5);

        assertEquals(15, stock.getQuantity());
    }

    @Test
    void shouldRejectInsufficientStock() {
        Stock stock = new Stock(product, location, 10);

        assertThrows(
                IllegalStateException.class,
                () -> stock.decreaseQuantity(11)
        );

        assertEquals(10, stock.getQuantity());
    }

    @Test
    void shouldRejectNegativeDecrease() {
        Stock stock = new Stock(product, location, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> stock.decreaseQuantity(-1)
        );
    }

    @Test
    void shouldRejectZeroIncrease() {
        Stock stock = new Stock(product, location, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> stock.increaseQuantity(0)
        );
    }

    @Test
    void shouldPreventIntegerOverflow() {
        Stock stock = new Stock(
                product,
                location,
                Integer.MAX_VALUE
        );

        assertThrows(
                ArithmeticException.class,
                () -> stock.increaseQuantity(1)
        );

        assertEquals(Integer.MAX_VALUE, stock.getQuantity());
    }

    @Test
    void shouldAllowStockToReachZero() {
        Stock stock = new Stock(product, location, 10);
        stock.decreaseQuantity(10);
        assertEquals(0, stock.getQuantity());
    }
}
