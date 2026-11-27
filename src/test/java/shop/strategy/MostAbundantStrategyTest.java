package shop.strategy;

import org.junit.jupiter.api.BeforeEach;
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
class MostAbundantStrategyTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private Location vienna;

    @Mock
    private Location graz;

    @InjectMocks
    private MostAbundantStrategy strategy;

    private final UUID laptopId = UUID.randomUUID();
    private final UUID keyboardId = UUID.randomUUID();

    private final UUID viennaId = UUID.randomUUID();
    private final UUID grazId = UUID.randomUUID();

    private final Product laptop = TestData.product();

    @BeforeEach
    void setUp() {
        when(locationRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(vienna, graz));

        when(vienna.getId()).thenReturn(viennaId);
        when(graz.getId()).thenReturn(grazId);
    }

    @Test
    void shouldSelectLocationWithMostStock() {
        Stock viennaStock = new Stock(laptop, vienna, 5);
        Stock grazStock = new Stock(laptop, graz, 10);

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
        assertSame(laptop, result.getFirst().product());
        assertEquals(3, result.getFirst().quantity());

        assertEquals(5, viennaStock.getQuantity());
        assertEquals(10, grazStock.getQuantity());
    }

    @Test
    void shouldSelectDifferentLocationsForDifferentProducts() {
        Product keyboard = TestData.product();
        keyboard.setName("Keyboard");

        Stock viennaLaptop = new Stock(laptop, vienna, 10);
        Stock grazLaptop = new Stock(laptop, graz, 4);

        Stock viennaKeyboard = new Stock(keyboard, vienna, 1);
        Stock grazKeyboard = new Stock(keyboard, graz, 8);

        when(stockRepository.findById(
                new StockId(laptopId, viennaId)))
                .thenReturn(Optional.of(viennaLaptop));

        when(stockRepository.findById(
                new StockId(laptopId, grazId)))
                .thenReturn(Optional.of(grazLaptop));

        when(stockRepository.findById(
                new StockId(keyboardId, viennaId)))
                .thenReturn(Optional.of(viennaKeyboard));

        when(stockRepository.findById(
                new StockId(keyboardId, grazId)))
                .thenReturn(Optional.of(grazKeyboard));

        List<OrderItem> items = List.of(
                new OrderItem(laptopId, 2),
                new OrderItem(keyboardId, 3)
        );

        List<StockAllocation> result =
                strategy.selectLocations(items);

        assertEquals(2, result.size());

        assertSame(laptop, result.get(0).product());
        assertSame(vienna, result.get(0).location());
        assertEquals(2, result.get(0).quantity());

        assertSame(keyboard, result.get(1).product());
        assertSame(graz, result.get(1).location());
        assertEquals(3, result.get(1).quantity());
    }

    @Test
    void shouldSelectFirstLocationWhenQuantitiesAreEqual() {
        Stock viennaStock = new Stock(laptop, vienna, 10);
        Stock grazStock = new Stock(laptop, graz, 10);

        when(stockRepository.findById(
                new StockId(laptopId, viennaId)))
                .thenReturn(Optional.of(viennaStock));

        when(stockRepository.findById(
                new StockId(laptopId, grazId)))
                .thenReturn(Optional.of(grazStock));

        List<OrderItem> items = List.of(
                new OrderItem(laptopId, 2)
        );

        List<StockAllocation> result =
                strategy.selectLocations(items);

        assertEquals(1, result.size());
        assertSame(vienna, result.getFirst().location());
    }

    @Test
    void shouldRejectOrderWhenNoLocationHasEnoughStock() {
        Stock viennaStock = new Stock(laptop, vienna, 5);
        Stock grazStock = new Stock(laptop, graz, 4);

        when(stockRepository.findById(
                new StockId(laptopId, viennaId)))
                .thenReturn(Optional.of(viennaStock));

        when(stockRepository.findById(
                new StockId(laptopId, grazId)))
                .thenReturn(Optional.of(grazStock));

        List<OrderItem> items = List.of(
                new OrderItem(laptopId, 6)
        );

        assertThrows(InsufficientStockException.class,
                () -> strategy.selectLocations(items));

        assertEquals(5, viennaStock.getQuantity());
        assertEquals(4, grazStock.getQuantity());
    }

    @Test
    void shouldSkipLocationWithMissingStock() {
        Stock grazStock = new Stock(laptop, graz, 10);

        when(stockRepository.findById(
                new StockId(laptopId, viennaId)))
                .thenReturn(Optional.empty());

        when(stockRepository.findById(
                new StockId(laptopId, grazId)))
                .thenReturn(Optional.of(grazStock));

        List<OrderItem> items = List.of(
                new OrderItem(laptopId, 2)
        );

        List<StockAllocation> result =
                strategy.selectLocations(items);

        assertEquals(1, result.size());
        assertSame(graz, result.getFirst().location());
    }


}