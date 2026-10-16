
# Market Exchange

![Java CI](https://github.com/vdementev2049-star/market-exchange/actions/workflows/ci.yml/badge.svg)

A Java backend application that simulates a stock exchange order book with price-time priority matching.

Built with Java 17, Spring Boot, PostgreSQL, Hibernate, Docker, and Maven.

## Features

- REST API for creating, retrieving, and cancelling orders
- Buy and sell orders with limit prices
- Price-time priority matching engine
- Full and partial order execution
- Trade records with execution prices and quantities
- Best bid, best ask, and spread queries
- Transactional order processing
- Unit tests with JUnit 5 and Mockito
- PostgreSQL integration tests
- Interactive API documentation with Swagger UI
- Automated CI testing with GitHub Actions

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Application language |
| Spring Boot | REST API and business logic |
| Spring Data JPA / Hibernate | Database persistence |
| PostgreSQL 17 | Relational database |
| Maven | Build and dependency management |
| Docker Compose | Container orchestration |
| JUnit 5 / Mockito | Automated testing |
| Springdoc OpenAPI | API documentation |
| GitHub Actions | Continuous integration |

## Architecture

HTTP Client / Swagger UI
|
v
OrderController
|
v
OrderService
|
+-- Matching Engine
|
v
OrderRepository / TradeRepository
|
v
Hibernate / JDBC
|
v
PostgreSQL

## Matching Engine

Orders are matched using price-time priority:

1. The best available price receives priority.
2. For equal prices, earlier orders receive priority.
3. A trade executes when buy and sell prices overlap.
4. Orders may be filled completely or partially.
5. Unfilled quantities remain available in the order book.

Example:

| Side | Price | Quantity |
|---|---|---|
| SELL | $190 | 30 |
| SELL | $192 | 50 |
| BUY (incoming) | $195 | 100 |

The incoming BUY order executes two trades:

- 30 shares at $190
- 50 shares at $192

The remaining 20 shares stay in the order book.

## REST API

| Method | Endpoint | Description |
|---|---|---|
| POST | /orders | Create an order |
| GET | /orders | List all orders |
| GET | /orders/{id} | Get an order by ID |
| DELETE | /orders/{id} | Cancel an order |
| GET | /orders/best-bid/{symbol} | Get the best bid |
| GET | /orders/best-ask/{symbol} | Get the best ask |
| GET | /orders/spread/{symbol} | Get the bid-ask spread |

### Example request

POST /orders

```json
{
  "symbol": "AAPL",
  "side": "BUY",
  "price": 195.00,
  "quantity": 100
}
```

## Running with Docker

Requirements:

- Docker Desktop
- Docker Compose

Create a `.env` file in the project root:

```dotenv
DB_PASSWORD=your_local_database_password
```

Start the application:

```bash
docker compose up -d --build
```

The application will be available at:

- REST API: http://localhost:8080/orders
- Swagger UI: http://localhost:8080/swagger-ui/index.html

Stop the containers:

```bash
docker compose down
```

Database data is persisted using a Docker volume.

## Testing

The project includes 14 automated tests covering:

- Order state transitions
- Order cancellation
- Service logic
- Matching engine execution
- Partial fills and price validation
- PostgreSQL repository integration
- Spring application context startup

Tests require a running PostgreSQL database and appropriate datasource credentials.

Run tests using Maven:

```bash
./mvnw clean verify
```

On Windows:

```powershell
.\mvnw.cmd clean verify
```

GitHub Actions automatically runs the test suite on pushes and pull requests targeting `main`.

## Project Status
The matching engine demonstrates core exchange mechanics. Production-grade concurrency control, authentication, and deployment infrastructure are outside the current scope.
