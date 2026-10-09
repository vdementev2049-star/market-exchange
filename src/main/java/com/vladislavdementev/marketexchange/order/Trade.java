
package com.vladislavdementev.marketexchange.order; // Defines the package.

import jakarta.persistence.*; // Imports JPA annotations.
import java.math.BigDecimal; // Stores precise prices.
import java.time.LocalDateTime; // Stores execution timestamps.

@Entity // Marks this class as a database entity.
@Table(name = "trades") // Maps the entity to the trades table.
public class Trade { // Represents an executed trade.

    @Id // Marks the primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generates IDs automatically.
    private Long id; // Unique trade identifier.

    private Long buyOrderId; // ID of the BUY order.
    private Long sellOrderId; // ID of the SELL order.
    private String symbol; // Stock symbol.
    private BigDecimal price; // Actual execution price.
    private int quantity; // Number of shares executed.
    private LocalDateTime executedAt; // Time of execution.

    public Trade() { // Required by JPA.
    } // Ends the constructor.

    public Trade(Long buyOrderId, Long sellOrderId, String symbol,
                 BigDecimal price, int quantity) { // Creates a new trade.
        this.buyOrderId = buyOrderId; // Stores the buyer's order ID.
        this.sellOrderId = sellOrderId; // Stores the seller's order ID.
        this.symbol = symbol; // Stores the stock symbol.
        this.price = price; // Stores the execution price.
        this.quantity = quantity; // Stores the executed quantity.
    } // Ends the constructor.

    @PrePersist // Runs automatically before saving a new trade.
    public void setExecutedAt() { // Initializes the execution timestamp.
        this.executedAt = LocalDateTime.now(); // Records the current time.
    } // Ends setExecutedAt.

    public Long getId() { return id; } // Returns the trade ID.
    public Long getBuyOrderId() { return buyOrderId; } // Returns the BUY order ID.
    public Long getSellOrderId() { return sellOrderId; } // Returns the SELL order ID.
    public String getSymbol() { return symbol; } // Returns the stock symbol.
    public BigDecimal getPrice() { return price; } // Returns the execution price.
    public int getQuantity() { return quantity; } // Returns the executed quantity.
    public LocalDateTime getExecutedAt() { return executedAt; } // Returns the execution time.

} // Ends the Trade class.
