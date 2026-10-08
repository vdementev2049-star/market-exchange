package com.vladislavdementev.marketexchange.order; // Places this exception inside the order package

public class OrderNotFoundException extends RuntimeException { // Defines a custom unchecked exception for a missing order

    public OrderNotFoundException(Long id) { // Receives the id of the order that could not be found
        super("Order with id " + id + " was not found"); // Creates a readable error message
    } // Ends the constructor

} // Ends the exception class