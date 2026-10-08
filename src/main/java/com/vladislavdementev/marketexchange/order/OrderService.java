package com.vladislavdementev.marketexchange.order; // Places this service inside the order package

import org.springframework.stereotype.Service; // Imports the annotation used to mark business-logic classes
import java.util.List; // Imports the List collection type

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
    public Order createOrder(CreateOrderRequest request) { // Defines the business operation for creating a new order
        Order order = new Order( // Creates a new database entity
                request.symbol(), // Reads the stock symbol from the API request
                request.side(), // Reads BUY or SELL from the API request
                request.price(), // Reads the requested price
                request.quantity() // Reads the requested share quantity
        ); // Finishes creating the Order object

        return orderRepository.save(order); // Saves the order in PostgreSQL and returns the saved entity
    }
}