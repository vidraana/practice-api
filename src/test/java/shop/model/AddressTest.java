package shop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressTest {

    @Test
    void shouldAllowMissingCounty() {
        Address address = new Address("Austria", "Vienna", null, "Example Street 10");
        assertNull(address.getCounty());
    }

    @Test
    void shouldRejectMissingStreet() {
        assertThrows(IllegalArgumentException.class,
                () -> new Address("Austria", "Vienna", null, " "));
    }

    @Test
    void shouldRejectOverlongCity() {
        String longCity = "a".repeat(101);

        assertThrows(IllegalArgumentException.class,
                () -> new Address("Austria", longCity, null, "Street"));
    }

}
