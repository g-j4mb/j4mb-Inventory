# IMS — Inventory Management System 

A full-stack inventory management application built with Java, Spring Boot, PostgreSQL, and React.

IMS is designed to demonstrate maintainable backend engineering, domain-driven design principles, REST API development, database integrity, automated testing, and professional Git practices.

&#x20; **Project status:** In development. Features and architecture are being implemented incrementally.

## 

## 1\. Business Overview

IMS helps a single company manage its products, suppliers, purchasing, stock availability, and goods movement.

The system supports the operational lifecycle of inventory, from ordering products from suppliers to receiving goods, reserving stock, dispatching goods, and processing returns.

### Business Scope

* **Product Management:** Maintain product information, SKU, unit of measure, cost, and stock thresholds.
* **Supplier Management:** Maintain supplier records and contact information.
* **Purchase Orders:** Create and manage orders placed with suppliers.
* **Goods Receiving:** Record full or partial deliveries against purchase orders.
* **Stock Reservations:** Reserve available quantities for intended dispatch.
* **Dispatch:** Record goods leaving inventory and fulfil reservations.
* **Returns:** Record returned goods and determine whether they can be restocked.
* **Inventory Visibility:** Monitor stock balances, available quantities, low-stock alerts, and operational activity.

### Out of Scope

The initial version is limited to one company and excludes:

* Accounting ledger and financial accounting.
* Invoicing and payment processing.
* Multi-company and multi-warehouse management.
* Advanced procurement and enterprise resource planning features.

## 

## 2\. Core Domain Concepts

The domain model represents business concepts and the rules governing inventory operations.

|Domain concept|Responsibility|
|-|-|
|Product|Defines an inventory item and its stock-keeping identity.|
|Supplier|Represents a business supplying products.|
|Purchase Order|Records products ordered from a supplier and their ordered quantities.|
|Goods Receipt|Records quantities received against a purchase order, including partial deliveries.|
|Inventory|Represents stock balances and the rules governing stock changes.|
|Reservation|Allocates available stock for an intended dispatch.|
|Dispatch|Records the fulfilment and physical issue of reserved stock.|
|Return|Records goods returned after dispatch and their restocking eligibility.|
|Stock Movement|Provides an operational history of receipts, dispatches, and inventory adjustments.|

### Fundamental Inventory Rules

* Available quantity = on-hand quantity − reserved quantity.
* Reservations cannot exceed available stock.
* Goods received cannot exceed the outstanding purchase order quantity.
* Dispatches cannot exceed the eligible reserved quantity.
* Returns must reference a valid dispatch and respect the eligible return quantity.
* Damaged or rejected returns must not increase available stock.
* Stock-changing operations must preserve data consistency and execute transactionally.
* Normal business operations must never produce negative stock quantities.

Stock movements provide operational traceability; they are not an accounting ledger.

## 

## 3\. Architecture

IMS follows **Ports and Adapters (Hexagonal Architecture)**.

The objective is to keep business rules independent of frameworks, databases, and delivery mechanisms. Application use cases coordinate domain behavior through explicit ports, while adapters connect those ports to external systems.

### Architectural Layers

**Domain**

* Business entities, value objects, domain rules, and domain policies.
* No dependency on Spring Boot, JPA, HTTP, or database implementation details.

**Application**

* Use cases that coordinate business operations.
* Input ports define operations exposed to delivery mechanisms.
* Output ports define the external capabilities required by use cases.
* Transaction boundaries are applied to appropriate business operations.

**Adapters**

* Inbound adapters: REST controllers and request/response mapping.
* Outbound adapters: PostgreSQL persistence, repository implementations, and other external integrations.
* Framework-specific configuration and security integrations.

### Dependency Direction

```text
REST API
   |
   v
Inbound Adapter
   |
   v
Input Port
   |
   v
Application Use Case
   |
   +----> Domain Model
   |
   +----> Output Port
              ^
              |
       Persistence Adapter
              |
              v
          PostgreSQL
```

Dependencies point inward toward application contracts and the domain. The domain does not depend on infrastructure.

## 

## 4\. Technology Stack

|Component|Technology|
|-|-|
|Language|Java 25|
|Backend framework|Spring Boot|
|Build tool|Maven|
|API|REST / JSON|
|Persistence|Spring Data JPA and Hibernate|
|Database|PostgreSQL|
|Database migrations|Flyway|
|Frontend|React, TypeScript, Vite|
|Authentication|Spring Security and JWT|
|Testing|JUnit 5, Mockito, integration tests|
|API documentation|OpenAPI / Swagger|
|Local database environment|Docker Compose|
|Version control|Git and GitHub|

The frontend and backend are separate applications. The frontend communicates with the backend through its REST API.

## 

## 5\. Repository Structure

```text
IMS/
├── backend/
│   └── src/
│       ├── main/
│       │   ├── java/com/stockflow/
│       │   │   ├── domain/
│       │   │   ├── application/
│       │   │   ├── adapter/
│       │   │   └── config/
│       │   └── resources/
│       │       └── db/migration/
│       └── test/
├── frontend/
│   └── src/
├── docs/
│   ├── architecture.md
│   ├── database-design.md
│   └── screenshots/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

This is the intended high-level structure. Package boundaries and module organization will be refined as use cases and domain responsibilities are implemented.

## 

## 6\. Development Roadmap

1. **Foundation:** Repository setup, Spring Boot, PostgreSQL, and Docker Compose.
2. **Database:** Flyway migrations and the initial product and supplier schema.
3. **Domain and application:** Domain models, ports, use cases, and persistence contracts.
4. **REST API:** CRUD endpoints, DTOs, validation, error handling, and API documentation.
5. **Authentication:** Registration, login, JWT, and protected endpoints.
6. **Inventory workflows:** Purchase orders, partial receiving, reservations, dispatch, returns, and stock movement history.
7. **Frontend:** Authentication, dashboard, master data, and operational workflows.
8. **Quality and delivery:** Automated tests, security review, logging, responsive UI, documentation, and GitHub presentation.

Each phase will be implemented, tested, reviewed, and committed before moving to the next.

## 

## 7\. Engineering Principles

* Keep domain rules independent of infrastructure frameworks.
* Express business operations as explicit application use cases.
* Define ports around application needs rather than database technology.
* Enforce data integrity through domain rules and database constraints.
* Use database transactions for operations that change stock.
* Test business rules independently from infrastructure.
* Use versioned database migrations instead of automatic schema generation.
* Keep secrets out of source control.
* Maintain small, meaningful Git commits.
* Document architectural decisions and important business rules.

## 

## 8\. Setup and Execution

The local development environment uses Docker Compose for PostgreSQL and Maven for the backend. The frontend is developed and run independently.

Detailed prerequisites, environment configuration, database initialization, backend startup, frontend startup, test commands, and troubleshooting instructions will be added after the project setup is verified.

## 

## 9\. Project Objective

IMS is a hands-on software engineering project intended to demonstrate the ability to design and implement a full-stack business application with explicit domain rules, clean architectural boundaries, reliable persistence, secure APIs, automated testing, and a professional development workflow.

