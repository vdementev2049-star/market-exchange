package com.vladislavdementev.marketexchange.order; // Places this controller inside the order package

import org.springframework.web.bind.annotation.GetMapping; // Lets a method handle HTTP GET requests
import org.springframework.web.bind.annotation.PostMapping; // Lets a method handle HTTP POST requests
import org.springframework.web.bind.annotation.RequestBody; // Converts incoming JSON into a Java object
import org.springframework.web.bind.annotation.RequestMapping; // Defines the base URL for this controller
import org.springframework.web.bind.annotation.RestController; // Marks this class as a REST API controller
import org.springframework.web.bind.annotation.PathVariable; // Reads a value directly from the URL path
import org.springframework.web.bind.annotation.DeleteMapping; // Lets a method handle HTTP DELETE requests
import jakarta.validation.Valid; // Enables validation of the incoming request object
import org.springframework.http.ResponseEntity;
import java.util.List; // Imports the List collection type
import java.math.BigDecimal; // Represents precise decimal values.

@RestController // Tells Spring to create this controller and use it for HTTP requests
@RequestMapping("/orders") // Makes /orders the base URL for this controller
public class OrderController { // Declares the controller class

    @GetMapping("/best-bid/{symbol}") // Handles GET requests for the best BUY price.
    public ResponseEntity<Order> getBestBid(@PathVariable String symbol) { // Reads the stock symbol.
        return ResponseEntity.of(orderService.getBestBid(symbol)); // Returns the order or HTTP 404.
    } // Ends getBestBid.

    @GetMapping("/best-ask/{symbol}") // Handles GET requests for the best SELL price.
    public ResponseEntity<Order> getBestAsk(@PathVariable String symbol) { // Reads the stock symbol.
        return ResponseEntity.of(orderService.getBestAsk(symbol)); // Returns the order or HTTP 404.
    } // Ends getBestAsk.

    @GetMapping("/{id}") // Handles HTTP GET requests such as /orders/2
    public Order getOrderById(@PathVariable Long id) { // Reads the id value from the URL
        return orderService.getOrderById(id); // Delegates the search operation to the service layer
    }

    @DeleteMapping("/{id}") // Handles HTTP DELETE requests such as /orders/2
    public void deleteOrder(@PathVariable Long id) { // Reads the id value from the URL
        orderService.deleteOrder(id); // Delegates the delete operation to the service layer
    }

    private final OrderService orderService; // Stores access to the business-logic service

    public OrderController(OrderService orderService) { // Spring injects the service into this controller
        this.orderService = orderService; // Saves the injected service
    }

    @GetMapping("/spread/{symbol}") // Handles requests for the bid-ask spread.
    public ResponseEntity<BigDecimal> getSpread(@PathVariable String symbol) { // Reads the stock symbol.
        return ResponseEntity.of(orderService.getSpread(symbol)); // Returns the spread or HTTP 404.
    } // Ends getSpread.

    @GetMapping // Handles HTTP GET requests sent to /orders
    public List<Order> getAllOrders() { // Defines the Java method called for this endpoint
        return orderService.getAllOrders(); // Delegates the operation to the service layer
    }

    @PostMapping // Handles POST /orders.
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) { // Reads and validates the incoming order.
        return orderService.createOrder(request); // Executes the matching logic through the service.
    } // Ends the method.

}