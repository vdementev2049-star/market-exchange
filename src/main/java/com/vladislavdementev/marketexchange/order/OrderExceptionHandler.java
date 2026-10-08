package com.vladislavdementev.marketexchange.order; // Places this handler inside the order package

import org.springframework.http.HttpStatus; // Imports standard HTTP status codes
import org.springframework.http.ResponseEntity; // Represents a complete HTTP response
import org.springframework.web.bind.annotation.ExceptionHandler; // Marks a method that handles a specific exception
import org.springframework.web.bind.annotation.RestControllerAdvice; // Applies exception handling to REST controllers

@RestControllerAdvice // Tells Spring to use this class for REST API error handling
public class OrderExceptionHandler { // Declares the exception handler class

    @ExceptionHandler(OrderNotFoundException.class) // Runs this method when OrderNotFoundException is thrown
    public ResponseEntity<String> handleOrderNotFound(OrderNotFoundException exception) { // Receives the thrown exception
        return ResponseEntity // Starts building the HTTP response
                .status(HttpStatus.NOT_FOUND) // Sets HTTP status to 404 Not Found
                .body(exception.getMessage()); // Sends the exception message as the response body
    } // Ends the handler method

} // Ends the handler class