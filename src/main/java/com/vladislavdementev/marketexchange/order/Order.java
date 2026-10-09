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

}














