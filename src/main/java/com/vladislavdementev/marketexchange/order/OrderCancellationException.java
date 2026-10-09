
package com.vladislavdementev.marketexchange.order; // Defines the package.

import org.springframework.http.HttpStatus; // Provides HTTP status codes.
import org.springframework.web.bind.annotation.ResponseStatus; // Maps exceptions to HTTP statuses.

@ResponseStatus(HttpStatus.CONFLICT) // Returns HTTP 409 when this exception occurs.
public class OrderCancellationException extends RuntimeException { // Defines a cancellation error.

    public OrderCancellationException(String message) { // Accepts an error message.
        super(message); // Passes the message to RuntimeException.
    } // Ends the constructor.

} // Ends the exception class.
