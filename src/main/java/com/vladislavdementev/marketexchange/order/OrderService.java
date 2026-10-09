package com.vladislavdementev.marketexchange.order; // Places this service inside the order package

import org.springframework.stereotype.Service; // Imports the annotation used to mark business-logic classes
import java.util.List; // Imports the List collection type
import java.util.Optional; // Handles a possibly missing order.
import org.springframework.transaction.annotation.Transactional; // Enables database transactions.
import java.math.BigDecimal; // Represents decimal numbers accurately.


@Service // Tells Spring to create and manage an instance of this service
public class OrderService { // Declares the service class

    private final OrderRepository orderRepository; // Stores access to the repository

    private final TradeRepository tradeRepository; // Provides access to executed trades.

    public List<Order> getAllOrders() { // Defines the business operation for getting all orders
        return orderRepository.findAll(); // Loads all Order entities from PostgreSQL
    }
    public Order getOrderById(Long id) { // Defines the business operation for finding one order by its id
        return orderRepository.findById(id) // Searches PostgreSQL for an order with this id
                .orElseThrow(() -> new OrderNotFoundException(id)); // Throws our custom exception if no order exists
    }

    @Transactional // Executes all database changes as one transaction.
    public Order createOrder(CreateOrderRequest request) { // Processes a new order.

        Order incoming = new Order( // Creates the incoming order.
                request.symbol(), // Stock symbol.
                request.side(), // BUY or SELL.
                request.price(), // Limit price.
                request.quantity() // Requested quantity.
        );

        incoming = orderRepository.save(incoming); // Saves the order and generates its ID.

        while (incoming.getRemainingQuantity() > 0) { // Continues while shares remain.

            Optional<Order> match = findMatchingOrder(incoming); // Finds the best opposite order.

            if (match.isEmpty()) { // Checks whether a match exists.
                break; // Stops matching when no suitable order exists.
            }

            Order resting = match.get(); // Gets the existing order.

            int amount = Math.min(
                    incoming.getRemainingQuantity(), // Available incoming quantity.
                    resting.getRemainingQuantity() // Available resting quantity.
            ); // Chooses the smaller quantity.

            incoming.fill(amount); // Updates the incoming order.
            resting.fill(amount); // Updates the existing order.

            Long buyId = incoming.getSide() == OrderSide.BUY
                    ? incoming.getId() : resting.getId(); // Identifies the BUY order.

            Long sellId = incoming.getSide() == OrderSide.SELL
                    ? incoming.getId() : resting.getId(); // Identifies the SELL order.

            Trade trade = new Trade(
                    buyId, // Buyer order ID.
                    sellId, // Seller order ID.
                    incoming.getSymbol(), // Traded stock.
                    resting.getPrice(), // Execution uses the resting order price.
                    amount // Number of executed shares.
            );

            tradeRepository.save(trade); // Records the trade.
            orderRepository.save(resting); // Persists the resting order changes.
        }

        return orderRepository.save(incoming); // Persists and returns the incoming order.
    }


    @Transactional // Keeps database operations in one transaction.
    public void deleteOrder(Long id) { // Cancels an existing order.

        Order order = getOrderById(id); // Finds the order or throws HTTP 404.

        order.cancel(); // Changes the status to CANCELLED.

        orderRepository.save(order); // Persists the updated order.

    } // Ends deleteOrder.



    public OrderService(OrderRepository orderRepository,
                        TradeRepository tradeRepository) { // Receives both repositories from Spring.

        this.orderRepository = orderRepository; // Stores the order repository.
        this.tradeRepository = tradeRepository; // Stores the trade repository.

    } // Ends the constructor.
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
    }

    public Optional<BigDecimal> getSpread(String symbol) { // Calculates the bid-ask spread.
        Optional<Order> bestBid = getBestBid(symbol); // Finds the highest BUY price.
        Optional<Order> bestAsk = getBestAsk(symbol); // Finds the lowest SELL price.

        if (bestBid.isEmpty() || bestAsk.isEmpty()) { // Checks whether either side is missing.
            return Optional.empty(); // No spread can be calculated.
        } // Ends the condition.

        BigDecimal spread = bestAsk.get().getPrice()
                .subtract(bestBid.get().getPrice()); // Subtracts the best bid from the best ask.

        return Optional.of(spread); // Returns the calculated spread.
    }

    private Optional<Order> findMatchingOrder(Order incoming) { // Finds an executable opposite order.

        Optional<Order> opposite; // Stores the best opposite order.

        if (incoming.getSide() == OrderSide.BUY) { // Checks whether the incoming order is BUY.
            opposite = getBestAsk(incoming.getSymbol()); // Finds the cheapest SELL order.
        } else { // Handles an incoming SELL order.
            opposite = getBestBid(incoming.getSymbol()); // Finds the highest BUY order.
        } // Ends side selection.

        if (opposite.isEmpty()) { // Checks whether an opposite order exists.
            return Optional.empty(); // No match is available.
        } // Ends the existence check.

        int comparison = incoming.getPrice().compareTo(opposite.get().getPrice()); // Compares the two limit prices.

        if (incoming.getSide() == OrderSide.BUY && comparison < 0) { // BUY price is too low.
            return Optional.empty(); // Rejects the match.
        } // Ends the BUY price check.

        if (incoming.getSide() == OrderSide.SELL && comparison > 0) { // SELL price is too high.
            return Optional.empty(); // Rejects the match.
        } // Ends the SELL price check.

        return opposite; // Returns the matching order.
    } // Ends findMatchingOrder.

}
