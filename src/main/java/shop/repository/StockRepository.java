package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.Stock;
import shop.model.StockId;

public interface StockRepository
        extends JpaRepository<Stock, StockId> {
}
