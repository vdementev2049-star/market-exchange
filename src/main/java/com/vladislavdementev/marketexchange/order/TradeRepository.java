
package com.vladislavdementev.marketexchange.order; // Defines the package.

import org.springframework.data.jpa.repository.JpaRepository; // Imports the JPA repository interface.

public interface TradeRepository extends JpaRepository<Trade, Long> { // Provides database operations for Trade entities.

} // Ends the repository interface.
