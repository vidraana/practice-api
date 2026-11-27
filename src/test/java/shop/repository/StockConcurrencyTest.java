package shop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.support.PostgresTestConfiguration;
import shop.ShopApplication;
import org.springframework.beans.factory.annotation.Autowired;
import shop.model.Stock;
import shop.model.StockId;
import shop.support.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Two overlapping independent transactions; no sleeps or thread scheduling required. */
@SpringBootTest(classes = {ShopApplication.class, PostgresTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class StockConcurrencyTest {

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void shouldRejectStaleStockUpdate() {
        StockId key = createCommittedStock();
        try {
            try (EntityManager first = emf.createEntityManager();
                 EntityManager second = emf.createEntityManager()) {
                try {
                    first.getTransaction().begin();
                    second.getTransaction().begin();
                    Stock a = first.find(Stock.class, key);
                    Stock b = second.find(Stock.class, key);
                    a.decreaseQuantity(7);
                    b.decreaseQuantity(7);
                    first.getTransaction().commit();
                    assertThrows(OptimisticLockException.class, second::flush);
                } finally {
                    rollbackIfActive(first);
                    rollbackIfActive(second);
                }
            }
            try (EntityManager verification = emf.createEntityManager()) {
                assertEquals(3, verification.find(Stock.class, key).getQuantity());
            }
        } finally {
            deleteCommittedStock(key);
        }
    }

    private StockId createCommittedStock() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Stock stock = new Stock(TestData.persistProduct(em), TestData.persistLocation(em), 10);
                em.persist(stock);
                em.getTransaction().commit();
                return stock.getId();
            } finally {
                rollbackIfActive(em);
            }
        }
    }

    private void deleteCommittedStock(StockId key) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Stock stock = em.find(Stock.class, key);
                var product = stock.getProduct();
                var location = stock.getLocation();
                var category = product.getCategory();
                em.remove(stock);
                em.flush();
                em.remove(product);
                em.remove(location);
                em.flush();
                em.remove(category);
                em.getTransaction().commit();
            } finally {
                rollbackIfActive(em);
            }
        }
    }

    private static void rollbackIfActive(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
