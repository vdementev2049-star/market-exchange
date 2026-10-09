package com.vladislavdementev.marketexchange.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    @Enumerated(EnumType.STRING)
    private OrderSide side;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING) // Stores the enum as text in PostgreSQL.
    private OrderStatus status; // Current order status.

    private int remainingQuantity; // Quantity that has not been filled yet.


    public Order() {
    }
    public Order(String symbol, OrderSide side, BigDecimal price, Integer quantity){
        this.symbol=symbol;
        this.price=price;
        this.side=side;
        this.quantity=quantity;

        this.status = OrderStatus.OPEN; // Every new order starts as OPEN.
        this.remainingQuantity = quantity; // Initially, all shares are unfilled.

    }
    @PrePersist
    public void setCreatedAt(){
        this.createdAt=LocalDateTime.now();
    }
    public Long getId(){
        return id;
    }
    public String getSymbol() {
        return symbol;
    }
    public OrderSide getSide() {
        return side;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() { // Returns the current order status.
        return status; // Returns the status field.
    }

    public int getRemainingQuantity() { // Returns the unfilled quantity.
        return remainingQuantity; // Returns the remaining quantity.
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void fill(int executedQuantity) { // Executes part or all of an order.

        if (status == OrderStatus.CANCELLED || status == OrderStatus.FILLED) { // Checks if the order is inactive.
            throw new IllegalStateException("Order cannot be filled"); // Rejects execution.
        } // Ends the status check.

        if (executedQuantity <= 0 || executedQuantity > remainingQuantity) { // Validates the execution quantity.
            throw new IllegalArgumentException("Invalid execution quantity"); // Rejects invalid quantities.
        } // Ends the quantity check.

        this.remainingQuantity -= executedQuantity; // Reduces the unfilled quantity.

        if (this.remainingQuantity == 0) { // Checks whether the order is fully executed.
            this.status = OrderStatus.FILLED; // Marks the order as completed.
        } else { // Handles a partially executed order.
            this.status = OrderStatus.PARTIALLY_FILLED; // Marks the order as partially filled.
        } // Ends the status update.

    } // Ends the fill method.

    public void cancel() { // Cancels an active order.

        if (this.status == OrderStatus.FILLED) { // Checks whether the order is fully executed.
            throw new OrderCancellationException("Filled orders cannot be cancelled"); // Rejects a filled order.
        }

        if (this.status == OrderStatus.CANCELLED) { // Checks whether the order is already cancelled.
            throw new OrderCancellationException("Order is already cancelled"); // Rejects repeated cancellation.
        }

        this.status = OrderStatus.CANCELLED; // Marks the order as cancelled.

    } // Ends cancel.

}














