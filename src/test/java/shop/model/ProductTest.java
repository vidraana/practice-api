package shop.model;

import org.junit.jupiter.api.Test;
import shop.support.TestData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    void shouldNormalizePriceToTwoDecimalPlaces() {
        Product product = TestData.product();
        product.setPrice(new BigDecimal("12.5"));
        assertEquals(new BigDecimal("12.50"), product.getPrice());
    }

    @Test
    void shouldRejectNegativePriceAndKeepPreviousValue() {
        Product product = TestData.product();
        BigDecimal negativePrice = new BigDecimal("-1");

        assertThrows(IllegalArgumentException.class,
                () -> product.setPrice(negativePrice));

        assertEquals(new BigDecimal("999.99"), product.getPrice());
    }

    @Test
    void shouldRejectExcessivePricePrecision() {
        Product product = TestData.product();
        BigDecimal excessivePrecision = new BigDecimal("0.001");
        BigDecimal excessivePrice = new BigDecimal("10000000000.00");

        assertThrows(IllegalArgumentException.class,
                () -> product.setPrice(excessivePrecision));

        assertThrows(IllegalArgumentException.class,
                () -> product.setPrice(excessivePrice));
    }

    @Test
    void shouldRejectNonFiniteOrNonPositiveWeight() {
        Product product = TestData.product();
        assertThrows(IllegalArgumentException.class, () -> product.setWeight(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> product.setWeight(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> product.setWeight(0.0));
    }

    @Test
    void shouldRejectBlankName() {
        Product product = TestData.product();

        assertThrows(IllegalArgumentException.class,
                () -> product.setName("  "));
    }

    @Test
    void shouldRejectExtremePriceExponentWithoutExpandingIt() {
        Product product = TestData.product();
        BigDecimal largePrice = new BigDecimal("1E+100000000");
        BigDecimal smallPrice = new BigDecimal("1E-100000000");

        assertThrows(IllegalArgumentException.class,
                () -> product.setPrice(largePrice));

        assertThrows(IllegalArgumentException.class,
                () -> product.setPrice(smallPrice));
    }
}
