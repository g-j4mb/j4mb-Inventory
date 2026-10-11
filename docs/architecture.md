# IMS — Architecture

## 1. Architectural Style

IMS follows **Ports and Adapters (Hexagonal Architecture)**, with feature-oriented package organization.

The architecture separates business rules from application workflows, delivery mechanisms, and infrastructure implementations.

## 2. Architectural Layers

### Domain
Core business concepts, entities, value objects, and invariants. Independent of Spring Boot, JPA, HTTP, and databases.

### Application
Coordinates use cases through input ports and output ports, orchestrating domain behavior and transactions.

### Inbound Adapters
REST controllers handle HTTP requests, validation, DTO mapping, and responses. They contain no core business logic.

### Outbound Adapters
Implement application output ports using PostgreSQL, Spring Data JPA, and other external integrations.

## 3. Dependency Rules

1. Domain does not depend on application frameworks or infrastructure.
2. Application depends on domain concepts and application-owned ports.
3. Inbound adapters depend on application input ports.
4. Outbound adapters implement application output ports.
5. Business rules do not reside in controllers or persistence adapters.

## 4. Package Organization

```text
com.IMS
├── product
│   ├── domain
│   ├── application
│   │   ├── port.in
│   │   ├── port.out
│   │   └── service
│   └── adapter
│       ├── in.web
│       └── out.persistence
├── supplier
├── purchasing
├── inventory
├── dispatch
├── returns
├── identity
└── config
```

The product feature establishes the initial pattern; other features will follow as implemented.

## 5. Request and Persistence Flow

REST requests enter through inbound adapters and invoke application input ports. Application use cases coordinate domain behavior and call output ports. Outbound adapters implement those ports using PostgreSQL or other external systems.

Dependencies point inward toward application contracts and the domain.

## 6. Database and Transactions

PostgreSQL is the primary database. Flyway manages versioned schema migrations, and Hibernate schema auto-generation is disabled.

Stock-changing operations use appropriate transactions and database constraints to preserve data consistency.

## 7. Testing Strategy

- **Domain tests:** Verify business rules independently.
- **Application tests:** Verify use cases and port interactions.
- **Adapter tests:** Verify REST mappings and persistence behavior.
- **Integration tests:** Verify migrations, transactions, and application behavior.
- **Architecture tests:** Use ArchUnit to enforce package boundaries, dependency direction, and domain independence.

Architecture tests must fail when code violates the defined dependency rules.

## 8. Architecture Evolution

Introduce abstractions when they establish meaningful boundaries. Avoid unnecessary interfaces, premature microservices, and abstractions without a clear purpose. Update this document as the implementation evolves.