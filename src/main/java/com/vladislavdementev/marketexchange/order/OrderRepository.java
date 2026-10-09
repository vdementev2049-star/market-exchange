package com.vladislavdementev.marketexchange.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional; // Represents a result that may be absent.
import java.util.Collection; // Represents a collection of values.


public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findFirstBySymbolAndSideAndStatusInOrderByPriceDescCreatedAtAsc(
            String symbol, // Stock symbol, such as AAPL.
            OrderSide side, // BUY or SELL.
            Collection<OrderStatus> statuses // Allowed order statuses.
    );

    Optional<Order> findFirstBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
            String symbol, // Stock symbol, such as AAPL.
            OrderSide side, // BUY or SELL.
            Collection<OrderStatus> statuses // Allowed order statuses.
    );


}