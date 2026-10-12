
package com.vladislavdementev.marketexchange.order; // Defines the test package.

import org.junit.jupiter.api.Test; // Provides JUnit tests.
import java.math.BigDecimal; // Provides precise decimal values.
import java.util.Optional; // Wraps an optional repository result.
import static org.junit.jupiter.api.Assertions.assertThrows; // Checks that an exception is thrown.
import static org.junit.jupiter.api.Assertions.assertSame; // Checks object identity.
import static org.mockito.Mockito.mock; // Creates mock objects.
import static org.mockito.Mockito.when; // Configures mock behavior.
import static org.mockito.Mockito.verify; // Verifies method calls.
import static org.junit.jupiter.api.Assertions.assertEquals; // Compares expected and actual values.
import static org.mockito.ArgumentMatchers.any; // Matches any argument of a given type.
import static org.mockito.Mockito.never; // Verifies a method was never called.
import static org.mockito.Mockito.times; // Verifies the number of method calls.
import java.util.List; // Provides lists of order statuses.
import org.mockito.ArgumentCaptor; // Captures objects passed to mock methods.



public class OrderServiceTest { // Contains service unit tests.

    @Test // Marks the method as a test.
    void shouldFindOrderById() { // Tests retrieving an existing order.

        OrderRepository orderRepository = mock(OrderRepository.class); // Creates a fake repository.
        TradeRepository tradeRepository = mock(TradeRepository.class); // Creates another fake repository.

        Order order = new Order( // Creates a real Order object.
                "AAPL", // Stock symbol.
                OrderSide.BUY, // BUY order.
                new BigDecimal("190.00"), // Price.
                100 // Quantity.
        );

        when(orderRepository.findById(42L))
                .thenReturn(Optional.of(order)); // Returns our order when ID 42 is requested.

        OrderService service = new OrderService(
                orderRepository, // Injects the fake order repository.
                tradeRepository // Injects the fake trade repository.
        );

        Order result = service.getOrderById(42L); // Calls the real service method.

        assertSame(order, result); // Checks that the same object was returned.
        verify(orderRepository).findById(42L); // Checks that the repository was called.
    } // Ends the test method.

    @Test // Marks this method as a JUnit test.
    void shouldThrowWhenOrderNotFound() { // Tests a missing order.

        OrderRepository orderRepository = mock(OrderRepository.class); // Creates a fake order repository.
        TradeRepository tradeRepository = mock(TradeRepository.class); // Creates a fake trade repository.

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty()); // Simulates an order that does not exist.

        OrderService service = new OrderService(
                orderRepository, // Injects the fake order repository.
                tradeRepository // Injects the fake trade repository.
        );

        assertThrows(
                OrderNotFoundException.class, // Expected exception type.
                () -> service.getOrderById(999L) // Attempts to find a missing order.
        );

        verify(orderRepository).findById(999L); // Verifies that the repository was called.
    } // Ends the test method.

    @Test // Marks the method as a JUnit test.
    void shouldCreateOpenOrderWhenNoMatchExists() { // Tests order creation without a matching seller.

        OrderRepository orders = mock(OrderRepository.class); // Creates a mock order repository.
        TradeRepository trades = mock(TradeRepository.class); // Creates a mock trade repository.

        when(orders.save(any(Order.class)))
                .thenAnswer(call -> call.getArgument(0)); // Returns the saved object without a database.

        OrderService service = new OrderService(orders, trades); // Creates the real service.

        CreateOrderRequest request = new CreateOrderRequest(
                "AAPL", // Stock symbol.
                OrderSide.BUY, // Order side.
                new BigDecimal("190.00"), // Limit price.
                100 // Requested quantity.
        );

        Order result = service.createOrder(request); // Processes the new order.

        assertEquals(OrderStatus.OPEN, result.getStatus()); // Checks the initial status.
        assertEquals(100, result.getRemainingQuantity()); // Checks the remaining shares.

        verify(orders, times(2)).save(any(Order.class)); // Checks both save calls.
        verify(trades, never()).save(any(Trade.class)); // Checks that no trade was created.
    }

    @Test // Marks this method as a test.
    void shouldMatchBuyWithExistingSell() { // Tests a BUY matching an existing SELL.

        OrderRepository orders = mock(OrderRepository.class); // Creates a mock repository.
        TradeRepository trades = mock(TradeRepository.class); // Creates a mock trade repository.

        Order seller = new Order(
                "AAPL", OrderSide.SELL, new BigDecimal("190.00"), 100
        ); // Creates an existing SELL order.

        when(orders.save(any(Order.class)))
                .thenAnswer(call -> call.getArgument(0)); // Returns the received order.

        when(orders.findFirstBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
                "AAPL", // Requested symbol.
                OrderSide.SELL, // Opposite side.
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED) // Active statuses.
        )).thenReturn(Optional.of(seller)); // Simulates finding the seller.

        OrderService service = new OrderService(orders, trades); // Creates the real service.

        CreateOrderRequest request = new CreateOrderRequest(
                "AAPL", OrderSide.BUY, new BigDecimal("195.00"), 60
        ); // Creates an incoming BUY request.

        Order buyer = service.createOrder(request); // Runs the matching engine.

        assertEquals(OrderStatus.FILLED, buyer.getStatus()); // Checks the buyer status.
        assertEquals(0, buyer.getRemainingQuantity()); // Checks the buyer quantity.

        assertEquals(OrderStatus.PARTIALLY_FILLED, seller.getStatus()); // Checks the seller status.
        assertEquals(40, seller.getRemainingQuantity()); // Checks the seller quantity.

        ArgumentCaptor<Trade> captor = ArgumentCaptor.forClass(Trade.class); // Prepares to capture a trade.
        verify(trades).save(captor.capture()); // Captures the saved trade.

        Trade trade = captor.getValue(); // Retrieves the captured trade.

        assertEquals(60, trade.getQuantity()); // Checks the traded quantity.
        assertEquals(new BigDecimal("190.00"), trade.getPrice()); // Checks the execution price.
    }

    @Test // Tests matching against multiple sellers.
    void shouldMatchMultipleSellers() {

        OrderRepository orders = mock(OrderRepository.class); // Mock order storage.
        TradeRepository trades = mock(TradeRepository.class); // Mock trade storage.

        Order seller1 = new Order(
                "AAPL", OrderSide.SELL, new BigDecimal("190.00"), 30
        ); // Creates the cheaper seller.

        Order seller2 = new Order(
                "AAPL", OrderSide.SELL, new BigDecimal("192.00"), 50
        ); // Creates the more expensive seller.

        when(orders.save(any(Order.class)))
                .thenAnswer(call -> call.getArgument(0)); // Returns the received order.

        when(orders.findFirstBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
                "AAPL",
                OrderSide.SELL,
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED)
        )).thenReturn(
                Optional.of(seller1), // First lookup returns seller1.
                Optional.of(seller2), // Second lookup returns seller2.
                Optional.empty() // Third lookup finds no seller.
        );

        OrderService service = new OrderService(orders, trades); // Creates the real service.

        CreateOrderRequest request = new CreateOrderRequest(
                "AAPL", OrderSide.BUY, new BigDecimal("195.00"), 100
        ); // Creates the incoming BUY request.

        Order buyer = service.createOrder(request); // Runs the matching engine.

        assertEquals(20, buyer.getRemainingQuantity()); // Checks remaining shares.
        assertEquals(OrderStatus.PARTIALLY_FILLED, buyer.getStatus()); // Checks buyer status.

        assertEquals(OrderStatus.FILLED, seller1.getStatus()); // Checks first seller.
        assertEquals(OrderStatus.FILLED, seller2.getStatus()); // Checks second seller.

        ArgumentCaptor<Trade> captor = ArgumentCaptor.forClass(Trade.class); // Captures trades.
        verify(trades, times(2)).save(captor.capture()); // Verifies two trades.

        List<Trade> savedTrades = captor.getAllValues(); // Gets both trade objects.

        assertEquals(30, savedTrades.get(0).getQuantity()); // First trade quantity.
        assertEquals(new BigDecimal("190.00"), savedTrades.get(0).getPrice()); // First price.

        assertEquals(50, savedTrades.get(1).getQuantity()); // Second trade quantity.
        assertEquals(new BigDecimal("192.00"), savedTrades.get(1).getPrice()); // Second price.
    }

    @Test // Tests orders with incompatible prices.
    void shouldNotMatchWhenBuyPriceIsTooLow() {

        OrderRepository orders = mock(OrderRepository.class); // Creates a mock order repository.
        TradeRepository trades = mock(TradeRepository.class); // Creates a mock trade repository.

        Order seller = new Order(
                "AAPL", OrderSide.SELL, new BigDecimal("200.00"), 50
        ); // Creates a seller asking $200.

        when(orders.save(any(Order.class)))
                .thenAnswer(call -> call.getArgument(0)); // Simulates saving an order.

        when(orders.findFirstBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
                "AAPL", // Stock symbol.
                OrderSide.SELL, // Finds SELL orders.
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED) // Active statuses.
        )).thenReturn(Optional.of(seller)); // Returns the existing seller.

        OrderService service = new OrderService(orders, trades); // Creates the real service.

        CreateOrderRequest request = new CreateOrderRequest(
                "AAPL", OrderSide.BUY, new BigDecimal("190.00"), 100
        ); // Creates a buyer offering only $190.

        Order buyer = service.createOrder(request); // Runs matching logic.

        assertEquals(OrderStatus.OPEN, buyer.getStatus()); // Buyer stays open.
        assertEquals(100, buyer.getRemainingQuantity()); // Buyer retains all shares.
        assertEquals(OrderStatus.OPEN, seller.getStatus()); // Seller stays open.
        assertEquals(50, seller.getRemainingQuantity()); // Seller retains all shares.

        verify(trades, never()).save(any(Trade.class)); // Verifies no trade was created.
    }


} // Ends the test class.
