package shop.repository;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.support.PostgresTestConfiguration;
import shop.ShopApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import shop.model.Product;
import shop.support.TestData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {ShopApplication.class, PostgresTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class ProductRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductRepository products;

    @Autowired
    private ProductCategoryRepository categories;

    @Test
    void shouldPersistProductAndFetchCategory() {
        Product product = TestData.product();
        categories.save(product.getCategory());
        products.save(product);
        em.flush();
        em.clear();
        Product loaded = products.findById(product.getId()).orElseThrow();
        em.clear();
        // Category is still available after detachment because of the entity graph.
        assertEquals("Electronics", loaded.getCategory().getName());
        assertEquals(new BigDecimal("999.99"), loaded.getPrice());
    }

    @Test
    void shouldFindProductsByCategoryAndName() {
        Product product = TestData.persistProduct(em);
        em.flush();
        em.clear();
        assertTrue(products.findByName("Laptop").stream()
                .anyMatch(found -> product.getId().equals(found.getId())));
        assertTrue(products.findByCategory_Id(product.getCategory().getId()).stream()
                .anyMatch(found -> product.getId().equals(found.getId())));
    }

    @Test
    void shouldUpdatePrice() {
        Product product = TestData.persistProduct(em);
        em.flush();
        em.clear();
        Product loaded = products.findById(product.getId()).orElseThrow();
        loaded.setPrice(new BigDecimal("899.99"));
        em.flush();
        em.clear();
        assertEquals(new BigDecimal("899.99"), products.findById(product.getId()).orElseThrow().getPrice());
    }

    @Test
    void shouldDeleteUnreferencedProductWithoutDeletingCategory() {
        Product product = TestData.persistProduct(em);
        em.flush();
        em.clear();
        products.deleteById(product.getId());
        em.flush();
        em.clear();
        assertFalse(products.existsById(product.getId()));
        assertTrue(categories.existsById(product.getCategory().getId()));
    }

    @Test
    void shouldRejectNaNPriceInDatabase() {
        Product product = TestData.persistProduct(em);
        em.flush();
        em.clear();

        var query = em.createNativeQuery(
                        "UPDATE online_shop.product SET price = 'NaN'::numeric WHERE id = :id")
                .setParameter("id", product.getId());

        assertThrows(PersistenceException.class,
                query::executeUpdate);
    }

    @Test
    void shouldRejectInfiniteWeightInDatabase() {
        Product product = TestData.persistProduct(em);
        em.flush();
        em.clear();

        var query = em.createNativeQuery(
                        "UPDATE online_shop.product SET weight = 'Infinity'::double precision WHERE id = :id")
                .setParameter("id", product.getId());

        assertThrows(PersistenceException.class,
                query::executeUpdate);
    }
}
