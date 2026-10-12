
package com.vladislavdementev.marketexchange.order; // Defines the test package.

import org.junit.jupiter.api.Test; // Enables JUnit tests.
import org.springframework.beans.factory.annotation.Autowired; // Injects Spring dependencies.
import org.springframework.boot.test.context.SpringBootTest; // Starts the Spring application context.
import org.springframework.transaction.annotation.Transactional; // Rolls back test database changes.

import java.math.BigDecimal; // Stores precise prices.
import java.util.List; // Provides lists.
import java.util.Optional; // Represents an optional result.
import java.util.UUID; // Generates a unique test symbol.

import static org.junit.jupiter.api.Assertions.assertEquals; // Compares expected and actual values.

@SpringBootTest // Starts Spring with the real repository.
@Transactional // Rolls back database changes after each test.
class OrderRepositoryIntegrationTest {

    @Autowired // Injects the real Spring Data repository.
    private OrderRepository orderRepository;

    @Test // Marks this method as a test.
    void shouldFindCheapestSellOrder() {

        String symbol = "TEST" + UUID.randomUUID().toString().substring(0, 8); // Avoids existing orders.

        Order expensive = new Order(
                symbol, OrderSide.SELL, new BigDecimal("192.00"), 50
        ); // Creates a more expensive SELL order.

        Order cheap = new Order(
                symbol, OrderSide.SELL, new BigDecimal("190.00"), 30
        ); // Creates a cheaper SELL order.

        orderRepository.saveAndFlush(expensive); // Writes the first order to PostgreSQL.
        orderRepository.saveAndFlush(cheap); // Writes the second order to PostgreSQL.

        Optional<Order> result =
                orderRepository.findFirstBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
                        symbol, // Test stock symbol.
                        OrderSide.SELL, // Searches SELL orders.
                        List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED) // Active orders.
                );

        Order bestAsk = result.orElseThrow(); // Gets the result or fails the test.

        assertEquals(cheap.getId(), bestAsk.getId()); // Verifies the cheapest order was selected.
    }

    @Test // Tests time priority for equal prices.
    void shouldSelectOlderOrderWhenPricesAreEqual() {

        String symbol = "TEST" + UUID.randomUUID().toString().substring(0, 8); // Unique test symbol.

        Order first = new Order(
                symbol, OrderSide.SELL, new BigDecimal("190.00"), 30
        ); // Creates the first seller.

        Order second = new Order(
                symbol, OrderSide.SELL, new BigDecimal("190.00"), 50
        ); // Creates the second seller.

        orderRepository.saveAndFlush(first); // Saves the older order first.
        orderRepository.saveAndFlush(second); // Saves the newer order second.

        Order bestAsk = orderRepository
                .findFirstBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
                        symbol, // Test symbol.
                        OrderSide.SELL, // Searches sellers.
                        List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED) // Active orders.
                )
                .orElseThrow(); // Requires a matching result.

        assertEquals(first.getId(), bestAsk.getId()); // Verifies that the older order wins.
    }

}
