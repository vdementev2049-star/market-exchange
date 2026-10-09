package com.vladislavdementev.marketexchange.order; // Places this service inside the order package

import org.springframework.stereotype.Service; // Imports the annotation used to mark business-logic classes
import java.util.List; // Imports the List collection type
import java.util.Optional; // Handles a possibly missing order.


@Service // Tells Spring to create and manage an instance of this service
public class OrderService { // Declares the service class

    private final OrderRepository orderRepository; // Stores access to the repository
    public List<Order> getAllOrders() { // Defines the business operation for getting all orders
        return orderRepository.findAll(); // Loads all Order entities from PostgreSQL
    }
    public Order getOrderById(Long id) { // Defines the business operation for finding one order by its id
        return orderRepository.findById(id) // Searches PostgreSQL for an order with this id
                .orElseThrow(() -> new OrderNotFoundException(id)); // Throws our custom exception if no order exists
    }

    public void deleteOrder(Long id) { // Defines the business operation for deleting one order by its id
        if (!orderRepository.existsById(id)) { // Checks whether the order exists before trying to delete it
            throw new OrderNotFoundException(id); // Throws our custom exception if the order does not exist
        } // Ends the existence check

        orderRepository.deleteById(id); // Deletes the existing order from PostgreSQL
    } // Ends the deleteOrder method

    public OrderService(OrderRepository orderRepository) { // Spring injects the repository into this service
        this.orderRepository = orderRepository; // Saves the injected repository
    }

    public Order createOrder(CreateOrderRequest request) { // Creates a new order.
        Order order = new Order( // Creates an Order entity.
                request.symbol(), // Gets the stock symbol.
                request.side(), // Gets the order side.
                request.price(), // Gets the price.
                request.quantity() // Gets the quantity.
        ); // Finishes object creation.

        return orderRepository.save(order); // Saves the order.
    } // Ends createOrder.

    public Optional<Order> getBestBid(String symbol) { // Finds the best BUY order.
        return orderRepository.findFirstBySymbolAndSideAndStatusInOrderByPriceDescCreatedAtAsc(
                symbol, // Stock symbol.
                OrderSide.BUY, // Only BUY orders.
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED) // Active statuses.
        ); // Returns the result.
    }

    public Optional<Order> getBestAsk(String symbol) { // Finds the lowest active SELL order.
        return orderRepository.findFirstBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
                symbol, // Stock symbol.
                OrderSide.SELL, // Only SELL orders.
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED) // Active statuses.
        ); // Returns the best matching order, if one exists.
    } // Ends getBestAsk.
} // Ends getBestBid.
