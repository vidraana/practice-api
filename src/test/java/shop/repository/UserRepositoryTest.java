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
import org.springframework.dao.DataIntegrityViolationException;
import shop.model.User;
import shop.model.UserRole;
import shop.support.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {ShopApplication.class, PostgresTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserRepository users;

    @Test
    void shouldPersistCustomerRoleAndEmail() {
        User user = users.save(TestData.customer());
        em.flush();
        em.clear();
        User loaded = users.findById(user.getId()).orElseThrow();
        assertEquals(UserRole.CUSTOMER, loaded.getRole());
        assertEquals(user.getEmailAddress(), loaded.getEmailAddress());
    }

    @Test
    void shouldRejectDuplicateUsername() {
        User first = users.saveAndFlush(TestData.customer());
        User duplicate = new User("Test", "Duplicate", first.getUsername(),
                "!DISABLED_TEST_ACCOUNT!", "different-" + first.getId() + "@example.invalid", UserRole.CUSTOMER);
        assertThrows(DataIntegrityViolationException.class, () -> users.saveAndFlush(duplicate));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        User first = users.saveAndFlush(TestData.customer());
        User duplicate = new User("Test", "Duplicate", "different-" + first.getId(),
                "!DISABLED_TEST_ACCOUNT!", first.getEmailAddress(), UserRole.CUSTOMER);
        assertThrows(DataIntegrityViolationException.class, () -> users.saveAndFlush(duplicate));
    }
}
