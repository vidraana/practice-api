package shop.strategy;

import shop.model.OrderItem;
import shop.model.StockAllocation;

import java.util.List;

public interface OrderStrategy {

    List<StockAllocation> selectLocations(List<OrderItem> items);
}