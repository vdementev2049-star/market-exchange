
package com.vladislavdementev.marketexchange.order; // Defines the test package.

import org.junit.jupiter.api.Test; // Marks test methods.
import static org.junit.jupiter.api.Assertions.assertEquals; // Checks expected values.
import static org.junit.jupiter.api.Assertions.assertThrows; // Verifies that an exception is thrown.
import java.math.BigDecimal; // Represents precise prices.

public class OrderTest { // Contains tests for the Order class.

    @Test // Tells JUnit to execute this method.
    void shouldPartiallyFillOrder() { // Tests partial order execution.

        Order order = new Order( // Creates an order for testing.
                "AAPL",
                OrderSide.BUY,
                new BigDecimal("190.00"),
                100
        );

        order.fill(60); // Executes 60 shares.

        assertEquals(40, order.getRemainingQuantity()); // Verifies the remaining quantity.
        assertEquals(OrderStatus.PARTIALLY_FILLED, order.getStatus()); // Verifies the order status.
    }

    @Test // Marks this method as a JUnit test.
    void shouldFullyFillOrder() { // Tests complete order execution.

        Order order = new Order( // Creates a new test order.
                "AAPL", // Stock symbol.
                OrderSide.BUY, // Order side.
                new BigDecimal("190.00"), // Limit price.
                100 // Original quantity.
        ); // Finishes creating the order.

        order.fill(100); // Executes all 100 shares.

        assertEquals(0, order.getRemainingQuantity()); // Verifies that no shares remain.
        assertEquals(OrderStatus.FILLED, order.getStatus()); // Verifies that the order is fully filled.

    } // Ends the test method.

    @Test // Marks this method as a test.
    void shouldRejectExcessiveFill() { // Tests invalid execution quantity.

        Order order = new Order( // Creates a test order.
                "AAPL", // Stock symbol.
                OrderSide.BUY, // Order side.
                new BigDecimal("190.00"), // Limit price.
                100 // Available shares.
        );

        assertThrows(
                IllegalArgumentException.class, // Expected exception type.
                () -> order.fill(120) // Attempts to execute too many shares.
        );

        assertEquals(100, order.getRemainingQuantity()); // Verifies quantity was not changed.
        assertEquals(OrderStatus.OPEN, order.getStatus()); // Verifies status was not changed.
    }

    @Test // Marks this method as a JUnit test.
    void shouldCancelOpenOrder() { // Tests cancellation of an open order.

        Order order = new Order( // Creates a new order.
                "AAPL", // Stock symbol.
                OrderSide.BUY, // Order side.
                new BigDecimal("190.00"), // Limit price.
                100 // Original quantity.
        ); // Ends object creation.

        order.cancel(); // Cancels the order.

        assertEquals(OrderStatus.CANCELLED, order.getStatus()); // Verifies the cancelled status.
        assertEquals(100, order.getRemainingQuantity()); // Verifies that no shares were executed.

    } // Ends the test method.

    @Test // Marks this method as a JUnit test.
    void shouldRejectFillAfterCancellation() { // Tests execution of a cancelled order.

        Order order = new Order( // Creates a new order.
                "AAPL", // Stock symbol.
                OrderSide.BUY, // Order side.
                new BigDecimal("190.00"), // Limit price.
                100 // Original quantity.
        );

        order.cancel(); // Cancels the order first.

        assertThrows(
                IllegalStateException.class, // Expected exception type.
                () -> order.fill(50) // Attempts to execute a cancelled order.
        );

        assertEquals(OrderStatus.CANCELLED, order.getStatus()); // Verifies the status remains unchanged.
        assertEquals(100, order.getRemainingQuantity()); // Verifies the quantity remains unchanged.

    } // Ends the test method.



}
