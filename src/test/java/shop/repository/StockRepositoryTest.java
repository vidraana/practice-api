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
import shop.model.Stock;
import shop.model.StockId;
import shop.support.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ShopApplication.class, PostgresTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class StockRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private StockRepository stocks;

    @Test
    void shouldDeriveCompositeKeyAndPersistStock() {
        var product = TestData.persistProduct(em);
        var location = TestData.persistLocation(em);
        Stock stock = stocks.save(new Stock(product, location, 10));
        em.flush();
        em.clear();
        StockId key = new StockId(product.getId(), location.getId());
        assertEquals(key, stock.getId());
        assertNotNull(stock.getVersion());
        assertEquals(10, stocks.findById(key).orElseThrow().getQuantity());
    }

    @Test
    void shouldPersistQuantityChange() {
        var product = TestData.persistProduct(em);
        var location = TestData.persistLocation(em);
        Stock stock = stocks.save(new Stock(product, location, 10));
        em.flush();
        em.clear();
        Stock loaded = stocks.findById(stock.getId()).orElseThrow();
        loaded.decreaseQuantity(2);
        em.flush();
        em.clear();
        assertEquals(8, stocks.findById(stock.getId()).orElseThrow().getQuantity());
    }

    @Test
    void shouldDeleteStockByCompositeKey() {
        Stock stock = stocks.save(new Stock(TestData.persistProduct(em), TestData.persistLocation(em), 10));
        em.flush();
        em.clear();
        stocks.deleteById(stock.getId());
        em.flush();
        em.clear();
        assertFalse(stocks.existsById(stock.getId()));
    }
}
