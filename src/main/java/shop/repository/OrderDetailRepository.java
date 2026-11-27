package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.model.OrderDetail;
import shop.model.OrderDetailId;

public interface OrderDetailRepository
        extends JpaRepository<OrderDetail, OrderDetailId> {
}
