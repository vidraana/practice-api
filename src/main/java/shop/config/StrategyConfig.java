package shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.repository.LocationRepository;
import shop.repository.StockRepository;
import shop.strategy.MostAbundantStrategy;
import shop.strategy.OrderStrategy;
import shop.strategy.SingleLocationStrategy;

@Configuration
public class StrategyConfig {

    @Bean
    public OrderStrategy orderStrategy(
            @Value("${shop.order.strategy}") String strategy,
            LocationRepository locationRepository,
            StockRepository stockRepository) {

        return switch (strategy) {
            case "single-location" ->
                    new SingleLocationStrategy(
                            locationRepository, stockRepository);

            case "most-abundant" ->
                    new MostAbundantStrategy(
                            locationRepository, stockRepository);

            default ->
                    throw new IllegalArgumentException(
                            "Unknown order strategy: " + strategy);
        };
    }
}