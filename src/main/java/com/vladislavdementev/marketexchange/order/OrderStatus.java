package com.vladislavdementev.marketexchange.order;

public enum OrderStatus { // Defines the possible order states.
    OPEN,             // Order is waiting for a match.
    PARTIALLY_FILLED, // Some quantity has been executed.
    FILLED,           // Entire quantity has been executed.
    CANCELLED         // Order was cancelled.
} // Ends the enum.