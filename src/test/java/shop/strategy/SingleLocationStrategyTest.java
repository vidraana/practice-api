package shop.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.exception.InsufficientStockException;
import shop.model.*;
import shop.repository.LocationRepository;
import shop.repository.StockRepository;
import shop.support.TestData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SingleLocationStrategyTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private Location vienna;

    @Mock
    private Location graz;

    @InjectMocks
    private SingleLocationStrategy strategy;

    private final UUID laptopId = UUID.randomUUID();
    private final UUID keyboardId = UUID.randomUUID();

    private final UUID viennaId = UUID.randomUUID();
    private final UUID grazId = UUID.randomUUID();

    private final Product laptop = TestData.product();

    @Test
    void shouldSelectFirstSuitableLocation() {
        when(locationRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(vienna, graz));

        when(vienna.getId()).thenReturn(viennaId);

        Stock stock = new Stock(laptop, vienna, 5);

        StockId stockId = new StockId(laptopId, viennaId);

        when(stockRepository.findById(stockId))
                .thenReturn(Optional.of(stock));

        List<OrderItem> items = List.of(
                new OrderItem(laptopId, 2)
        );

        List<StockAllocation> result =
                strategy.selectLocations(items);

        assertEquals(1, result.size());
        assertSame(vienna, result.getFirst().location());
        assertSame(laptop, result.getFirst().product());
        assertEquals(2, result.getFirst().quantity());

        assertEquals(5, stock.getQuantity());

        verifyNoInteractions(graz);
    }

    @Test
    void shouldSkipLocationWithInsufficientStock() {
        when(locationRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(vienna, graz));

        when(vienna.getId()).thenReturn(viennaId);
        when(graz.getId()).thenReturn(grazId);

        Stock viennaStock = new Stock(laptop, vienna, 1);
        Stock grazStock = new Stock(laptop, graz, 8);

        when(stockRepository.findById(
                new StockId(laptopId, viennaId)))
                .thenReturn(Optional.of(viennaStock));

        when(stockRepository.findById(
                new StockId(laptopId, grazId)))
                .thenReturn(Optional.of(grazStock));

        List<OrderItem> items = List.of(
                new OrderItem(laptopId, 3)
        );

        List<StockAllocation> result =
                strategy.selectLocations(items);

        assertEquals(1, result.size());
        assertSame(graz, result.getFirst().location());
        assertEquals(3, result.getFirst().quantity());
    }

    @Test
    void shouldRejectOrderWhenStockIsSplitAcrossLocations() {
        when(locationRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(vienna, graz));

        when(vienna.getId()).thenReturn(viennaId);
        when(graz.getId()).thenReturn(grazId);

        Product keyboard = TestData.product();
        keyboard.setName("Keyboard");

        Stock viennaLaptop = new Stock(laptop, vienna, 10);
        Stock viennaKeyboard = new Stock(keyboard, vienna, 1);
        Stock grazLaptop = new Stock(laptop, graz, 1);

        when(stockRepository.findById(
                new StockId(laptopId, viennaId)))
                .thenReturn(Optional.of(viennaLaptop));

        when(stockRepository.findById(
                new StockId(keyboardId, viennaId)))
                .thenReturn(Optional.of(viennaKeyboard));

        when(stockRepository.findById(
                new StockId(laptopId, grazId)))
                .thenReturn(Optional.of(grazLaptop));

        List<OrderItem> items = List.of(
                new OrderItem(laptopId, 2),
                new OrderItem(keyboardId, 3)
        );

        assertThrows(InsufficientStockException.class,
                () -> strategy.selectLocations(items));
    }

    @Test
    void shouldRejectOrderWhenStockRecordIsMissing() {
        when(locationRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(vienna));

        when(vienna.getId()).thenReturn(viennaId);

        when(stockRepository.findById(
                new StockId(laptopId, viennaId)))
                .thenReturn(Optional.empty());

        List<OrderItem> items = List.of(
                new OrderItem(laptopId, 2)
        );

        assertThrows(InsufficientStockException.class,
                () -> strategy.selectLocations(items));
    }
}