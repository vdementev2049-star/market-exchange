package com.vladislavdementev.marketexchange.order; // Places this record inside the order package

import jakarta.validation.constraints.NotBlank; // Provides validation for non-empty text
import jakarta.validation.constraints.NotNull; // Provides validation for required values
import jakarta.validation.constraints.Positive; // Provides validation for numbers greater than zero
import java.math.BigDecimal; // Imports the precise decimal type used for money

public record CreateOrderRequest( // Defines the data that a client may send when creating an order
                                  @NotBlank String symbol, // Requires a non-empty stock ticker such as AAPL
                                  @NotNull OrderSide side, // Requires either BUY or SELL
                                  @NotNull @Positive BigDecimal price, // Requires a price and makes sure it is greater than zero
                                  @NotNull @Positive Integer quantity // Requires a quantity and makes sure it is greater than zero
) { // Opens the record body
} // Closes the record