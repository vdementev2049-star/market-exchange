# Market Exchange — Study Plan & Progress

## Project Goal

Build a portfolio-ready backend project for Software Developer / Java Backend applications using:

- Java 17
- Spring Boot
- REST API
- PostgreSQL
- Spring Data JPA / Hibernate
- Docker / Docker Compose
- Validation
- JUnit / Mockito
- Swagger / OpenAPI
- Git / GitHub
- GitHub Actions

Project idea: **Mini Exchange / Order Book REST API**

The application accepts BUY/SELL stock orders, stores them in PostgreSQL, exposes REST endpoints, and later will add order-book and matching logic.

---

## Current Checkpoint

**Current study day:** Day 3  
**Current progress:** ~99%  
**Current step:** Git/GitHub setup

### Already completed

- Created Spring Boot project with Maven
- Installed/configured Docker Desktop + WSL2
- Enabled hardware virtualization in BIOS
- Created PostgreSQL container with Docker Compose
- Connected Spring Boot to PostgreSQL
- Created `Order` JPA entity
- Created `OrderSide` enum (`BUY`, `SELL`)
- Added auto-generated `id`
- Added automatic `createdAt`
- Created `OrderRepository`
- Verified Hibernate created the `orders` table
- Created temporary seeder and verified Java → PostgreSQL persistence
- Removed the temporary seeder
- Created `CreateOrderRequest` DTO
- Created `OrderController`
- Added:
  - `POST /orders`
  - `GET /orders`
  - `GET /orders/{id}`
  - `DELETE /orders/{id}`
- Created `OrderService`
- Refactored Controller → Service → Repository architecture
- Added request validation
- Added `OrderNotFoundException`
- Added REST exception handler returning HTTP 404
- Verified HTTP requests using PowerShell
- Installed Git
- Initialized local Git repository
- Staged project files
- Created public GitHub repository: `market-exchange`

### Next immediate steps

1. Verify the first local Git commit was successfully created.
2. Connect the local repository to GitHub using `origin`.
3. Push branch `main` to GitHub.
4. Add this `STUDY_PLAN.md` file to the project.
5. Commit and push the study plan.
6. Finish Day 3.

---

# 7-Day Learning Plan

## Day 1 — Spring Boot + PostgreSQL + Docker

**Status: COMPLETE**

Covered:

- What Spring Boot is
- What PostgreSQL is
- What Docker is
- WSL2 / virtualization setup
- Docker Compose
- PostgreSQL container
- `application.properties`
- Successful Spring Boot → PostgreSQL connection

Key mental model:

`Spring Boot application → Hibernate/JPA → PostgreSQL`

---

## Day 2 — JPA Entity + Repository + Persistence

**Status: COMPLETE**

Covered:

- `Order` Java class
- `@Entity`
- `@Table`
- `@Id`
- `@GeneratedValue`
- `OrderSide` enum
- `@Enumerated`
- `@PrePersist`
- constructors
- getters/setters
- Hibernate
- JPA
- `OrderRepository`
- `JpaRepository`
- dependency injection
- verified data inside PostgreSQL

Key mental model:

`Java Order → Repository → Hibernate → SQL → PostgreSQL`

---

## Day 3 — REST API + Service Layer + Validation + Errors + Git

**Status: IN PROGRESS (~99%)**

Covered:

- HTTP basics
- REST API basics
- client/server concept
- DTO
- `CreateOrderRequest`
- `@RestController`
- `@RequestMapping`
- `@PostMapping`
- `@GetMapping`
- `@DeleteMapping`
- `@RequestBody`
- `@PathVariable`
- POST / GET / DELETE requests
- Controller → Service → Repository architecture
- `@Service`
- validation with `@Valid`, `@NotBlank`, `@NotNull`, `@Positive`
- custom exception
- HTTP 404 handling
- local Git basics
- GitHub repository creation

Still to finish:

- first Git commit verification
- GitHub remote setup
- first push
- add/push `STUDY_PLAN.md`

---

## Day 4 — Order Book + Matching Logic

**Status: NOT STARTED**

Planned:

- add order status
- distinguish open/filled/cancelled orders
- best bid
- best ask
- spread
- price-time priority
- match BUY and SELL orders
- partial fills
- remaining quantity
- trade creation
- move real business logic into Service layer

Goal:

Turn the project from a basic CRUD API into a finance/trading backend project.

---

## Day 5 — Testing

**Status: NOT STARTED**

Planned:

- JUnit 5
- unit tests
- Mockito
- service tests
- repository/integration tests
- matching-engine test cases
- invalid request tests
- 404 tests

Goal:

Be able to explain what is unit-tested and why.

---

## Day 6 — Dockerize App + Swagger/OpenAPI

**Status: NOT STARTED**

Planned:

- Dockerfile for Spring Boot app
- run both app + PostgreSQL through Docker Compose
- environment variables
- remove hard-coded DB credentials from normal config where appropriate
- Swagger/OpenAPI
- inspect/test endpoints in browser UI

Goal:

One command should launch the complete project.

---

## Day 7 — GitHub Actions + Resume Polish

**Status: NOT STARTED**

Planned:

- GitHub Actions CI
- automatic Maven build
- automatic tests
- README
- architecture overview
- API examples
- project screenshots / usage examples if useful
- cleanup / refactor
- final Git history
- resume bullet
- GitHub link added to resume

Goal:

Portfolio-ready repository suitable for Software Developer applications.

---

# Core Concepts to Be Able to Explain

By the end of the project, be able to explain these without memorizing every annotation:

- Java vs Spring vs Spring Boot
- HTTP
- REST API
- JSON
- Controller
- Service
- Repository
- DTO
- Entity
- JPA
- Hibernate
- PostgreSQL
- dependency injection
- Maven
- Docker
- Docker Compose
- Git
- GitHub
- unit test vs integration test
- CI / GitHub Actions

Target skill level:

> Understand the architecture, design the flow yourself, and write most of the implementation using IDE autocomplete/documentation. Memorizing every Spring annotation is not required.

---

# Current Architecture

```text
Client
  ↓ HTTP / JSON
OrderController
  ↓
OrderService
  ↓
OrderRepository
  ↓
Spring Data JPA / Hibernate
  ↓
PostgreSQL
```

Infrastructure:

```text
Spring Boot application
        +
PostgreSQL container
        ↓
Docker / Docker Compose
```

---

# Current API

```text
POST   /orders
GET    /orders
GET    /orders/{id}
DELETE /orders/{id}
```

Current request body:

```json
{
  "symbol": "AAPL",
  "side": "BUY",
  "price": 190.10,
  "quantity": 75
}
```

---

# Continuation Prompt for a New Chat

If a new chat is needed, attach or paste this file and say:

> Continue my Market Exchange Spring Boot project from the current checkpoint in STUDY_PLAN.md. Keep working in mini-steps, explain new terms before using them, comment every provided code line in English, and always tell me the current day and percentage.

