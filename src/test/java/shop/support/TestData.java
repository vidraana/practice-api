package shop.support;

import jakarta.persistence.EntityManager;
import shop.model.Address;
import shop.model.Location;
import shop.model.Product;
import shop.model.ProductCategory;
import shop.model.User;
import shop.model.UserRole;

import java.math.BigDecimal;
import java.util.UUID;

public final class TestData {

    private TestData() {
    }

    public static Address address() {
        return new Address("Austria", "Vienna", null, "Example Street 10");
    }

    public static Product product() {
        return new Product("Laptop", "Test product", new BigDecimal("999.99"),
                2.5, new ProductCategory("Electronics", null), null);
    }

    public static Location location() {
        return new Location("Warehouse", address());
    }

    public static User customer() {
        String suffix = UUID.randomUUID().toString();
        // Deliberately unusable test-only value; authentication is not tested here.
        return new User("Test", "Customer", "test-" + suffix,
                "!DISABLED_TEST_ACCOUNT!", suffix + "@example.invalid", UserRole.CUSTOMER);
    }

    public static Product persistProduct(EntityManager em) {
        Product product = product();
        em.persist(product.getCategory());
        em.persist(product);
        return product;
    }

    public static Location persistLocation(EntityManager em) {
        Location location = location();
        em.persist(location);
        return location;
    }

    public static User persistCustomer(EntityManager em) {
        User user = customer();
        em.persist(user);
        return user;
    }
}
