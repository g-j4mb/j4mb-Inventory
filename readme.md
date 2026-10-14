# IMS — Inventory Management System

IMS is a full-stack inventory management application built with Java, Spring Boot, PostgreSQL, and React with TypeScript.

The project demonstrates backend engineering practices, Domain-Driven Design (DDD), Ports and Adapters architecture, REST API development, JWT-based authentication, database migrations, and automated testing.

**Project status:** In development. Product management and authentication are implemented; additional inventory workflows are planned.

## 1\. Business Overview

IMS is designed for a single company that needs to manage products and inventory operations.

The current implementation focuses on product master data and authenticated API access. The broader business scope includes purchasing, stock availability, goods movement, and inventory control.

### Implemented functionality

* **Product management:** Create, retrieve, update, and deactivate products.
* **Product listing:** Search products and navigate through paginated results.
* **Soft deletion:** Deactivated products are excluded from the active product listing rather than permanently deleted.
* **Authentication:** Login using an email address and password.
* **JWT security:** Issue signed access tokens and protect API endpoints using Spring Security.
* **Database migrations:** Manage database schema changes using Flyway.
* **Automated testing:** Test product domain rules, application services, web endpoints, persistence, and authentication logic.

### Planned functionality

The following business capabilities are part of the intended scope but are not yet fully implemented:

* Supplier management workflows.
* Purchase order management.
* Full and partial goods receiving.
* Stock reservations.
* Dispatch and fulfilment.
* Returns and restocking eligibility.
* Stock movement history and inventory availability calculations.
* Expanded inventory dashboard and operational workflows.

### Out of scope for the initial version

* Accounting ledger and financial accounting.
* Invoicing and payment processing.
* Multi-company and multi-warehouse management.
* Advanced procurement and enterprise resource planning functionality.

## 2\. Architecture

IMS follows **Ports and Adapters (Hexagonal Architecture)**, organized by business feature.

The objective is to isolate business rules from infrastructure concerns and make application use cases independently testable.

### Domain layer

Contains the business model and domain rules.

The domain layer should not depend on Spring Boot, JPA, HTTP, or database implementation details.

### Application layer

Contains application use cases and ports.

* **Input ports** define the operations exposed to inbound adapters.
* **Output ports** define the capabilities required from external systems.
* **Application services** coordinate use cases and domain behavior.

### Infrastructure layer

Contains adapters that connect the application to external technologies.

* **Inbound adapters:** REST controllers and HTTP request/response handling.
* **Outbound persistence adapters:** Spring Data JPA and PostgreSQL integration.
* **Security adapters:** Password verification and JWT issuance.
* **Configuration:** Spring application configuration and security setup.

### Dependency direction

```text
HTTP Client / React Frontend
             |
             v
      Inbound Web Adapter
             |
             v
         Input Port
             |
             v
     Application Service
             |
       +-----+------+
       |            |
       v            v
   Domain Model  Output Ports
                      ^
                      |
              Outbound Adapters
                      |
                      v
                  PostgreSQL
```

Application services depend on ports rather than directly on infrastructure implementations. Spring configuration connects the interfaces to their adapters at runtime.

## 3\. Current Backend Package Structure

The backend uses the root package `com.j4mb.ims`.

```text
backend/src/main/java/com/j4mb/ims/
├── config/
│   ├── SecurityConfiguration.java
│   ├── JwtConfiguration.java
│   └── AdminBootstrapConfiguration.java
│
├── identity/
│   ├── domain/
│   ├── application/
│   │   ├── port/
│   │   │   ├── in/
│   │   │   └── out/
│   │   └── service/
│   └── infrastructure/
│       └── adapter/
│           ├── in/web/
│           └── out/
│               ├── persistence/
│               └── security/
│
└── product/
    ├── domain/
    ├── application/
    │   ├── exception/
    │   ├── port/
    │   │   ├── in/
    │   │   └── out/
    │   └── service/
    └── infrastructure/
        └── adapter/
            ├── in/web/
            └── out/persistence/
```

This tree summarizes the principal package boundaries; it is not an exhaustive list of source files.

The `product` feature contains product-domain behavior, application use cases, REST endpoints, and persistence integration. The `identity` feature separates authentication logic from user persistence and security-specific implementations.

## 4\. Technology Stack

|Component|Technology|
|-|-|
|Language|Java 21|
|Backend framework|Spring Boot 4.1.1|
|Build tool|Maven|
|API|REST / JSON|
|Persistence|Spring Data JPA and Hibernate|
|Database|PostgreSQL 17|
|Database migrations|Flyway|
|Frontend|React 19, TypeScript|
|Frontend tooling|Vite|
|Authentication|Spring Security and JWT|
|Password hashing|BCrypt|
|Automated testing|JUnit 5, Mockito, Spring test dependencies|
|Database integration testing|Testcontainers|
|Local database environment|Docker Compose|
|Version control|Git and GitHub|

The frontend and backend are separate applications. The frontend communicates with the backend through REST endpoints.

## 5\. Authentication and Security

IMS uses stateless JWT-based authentication.

### Login flow

1. The client submits an email address and password to `POST /api/auth/login`.
2. The backend loads the corresponding user through an application port.
3. The password verifier checks the supplied password against the stored password hash.
4. If the account is active and the credentials are valid, the application issues a signed JWT.
5. The client sends the token in the `Authorization` header when accessing protected endpoints.

Example request header:

```http
Authorization: Bearer <access-token>
```

### Security characteristics

* Spring Security protects endpoints that require authentication.
* Passwords are stored as BCrypt hashes rather than plaintext.
* JWTs are signed using HS256.
* The configured access-token lifetime is 900 seconds (15 minutes).
* The issuer is configured as `IMS`.
* Database-backed user persistence is separated from authentication use cases.
* An optional development-only bootstrap mechanism can create an initial administrator account.

The JWT signing secret must be supplied through environment configuration and must never be committed to source control.

## 6\. Database and Persistence

PostgreSQL is the primary database.

Docker Compose provides the local PostgreSQL environment, while Flyway manages versioned database migrations.

Hibernate schema generation is disabled through `ddl-auto: none`; schema changes are managed through migrations.

The local database is exposed on `127.0.0.1:5434`, mapped to PostgreSQL's container port `5432`.

A named Docker volume preserves database data across container restarts.

## 7\. Testing Strategy

The test suite is organized around the application and its architectural boundaries.

Current test coverage includes:

* **Domain tests:** Product-domain rules and behavior.
* **Application service tests:** Product creation, retrieval, listing, updating, deletion, and authentication decisions.
* **Web adapter tests:** Product controller behavior.
* **Persistence tests:** Product persistence integration.
* **Application context test:** Basic Spring Boot application initialization.

Testcontainers is available for database-backed integration testing and requires Docker to be running.

Run the complete backend test suite with:

```powershell
cd backend
.\\mvnw.cmd test
```

Run a specific test class with:

```powershell
.\\mvnw.cmd "-Dtest=AuthenticateUserServiceTest" test
```

Only tests that actually exist and pass in the current checkout should be considered verified. Additional HTTP-level security tests can extend coverage of missing, malformed, expired, and valid JWTs.

## 8\. Engineering Principles

* Keep domain rules independent of infrastructure frameworks.
* Express business operations through explicit application use cases.
* Define ports around application needs rather than database technology.
* Preserve clear dependency direction.
* Use database migrations instead of automatic schema generation.
* Enforce business invariants and database constraints.
* Test business behavior independently from infrastructure where practical.
* Keep secrets out of source control.
* Maintain small, meaningful Git commits.
* Document implementation status honestly and distinguish completed work from planned features.

## 9\. Installation and Setup

The following instructions describe a local development setup on Windows using PowerShell.

### Prerequisites

Install the following tools:

* Git
* Java Development Kit (JDK) 21
* Docker Desktop with Docker Compose
* Node.js and npm compatible with the installed Vite version

Verify the installations:

```powershell
java -version
git --version
docker --version
docker compose version
node --version
npm --version
```

### Step 1: Clone the repository

```powershell
git clone https://github.com/g-j4mb/j4mb-Inventory.git
cd j4mb-Inventory
```

### Step 2: Configure environment variables

Copy the example environment file:

```powershell
Copy-Item .env-example .env
```

Open `.env` and configure the following variables:

```dotenv
POSTGRES\_DB=ims
POSTGRES\_USER=ims\_app
POSTGRES\_PASSWORD=replace\_with\_a\_strong\_local\_password
POSTGRES\_URL=jdbc:postgresql://localhost:5434/ims
JWT\_SECRET\_BASE64=replace\_with\_generated\_base64\_secret
BOOTSTRAP\_ADMIN\_ENABLED=false
BOOTSTRAP\_ADMIN\_EMAIL=
BOOTSTRAP\_ADMIN\_PASSWORD=
```

Generate a random 32-byte JWT signing secret in PowerShell:

```powershell
$bytes = New-Object byte\[] 32
\[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
\[Convert]::ToBase64String($bytes)
```

Copy the resulting Base64 string into `JWT\_SECRET\_BASE64` in `.env`.

Use a strong local database password for `POSTGRES\_PASSWORD`. Keep the database credentials and JWT signing secret private.

**Important:** `.env` is for local use and must not be committed to Git. The example file is a template, not a file containing usable production credentials.

### Step 3: Start PostgreSQL

Make sure Docker Desktop is running, then execute this command from the repository root:

```powershell
docker compose up -d db
```

Check the database container:

```powershell
docker compose ps
```

Wait until the PostgreSQL health check reports that the database is healthy.

The backend connects to the database using:

```text
jdbc:postgresql://localhost:5434/ims
```

### Step 4: Start the backend

Open a terminal in the repository root and run:

```powershell
cd backend
.\\mvnw.cmd spring-boot:run
```

The backend reads the environment variables from the process environment. If the application does not automatically receive variables from the root `.env` file, load them into the PowerShell session before starting it:

```powershell
Get-Content ..\\.env |
    Where-Object { $\_ -match '^\\s\*\[A-Za-z\_]\[A-Za-z0-9\_]\*=' } |
    ForEach-Object {
        $name, $value = $\_ -split '=', 2
        \[Environment]::SetEnvironmentVariable(
            $name.Trim(),
            $value.Trim(),
            'Process'
        )
    }
```

Then run the Maven command again from the `backend` directory.

The application starts on:

```text
http://localhost:8080
```

Flyway applies the configured database migrations during application startup.

### Step 5: Configure the initial administrator (optional)

If an initial administrator account is required, use the development-only bootstrap mechanism.

Set these variables in the root `.env` file:

```dotenv
BOOTSTRAP\_ADMIN\_ENABLED=true
BOOTSTRAP\_ADMIN\_EMAIL=admin@example.com
BOOTSTRAP\_ADMIN\_PASSWORD=replace\_with\_a\_strong\_password
```

The bootstrap configuration is restricted to the `dev` Spring profile. To activate it, ensure that the backend starts with:

```powershell
$env:SPRING\_PROFILES\_ACTIVE = "dev"
```

Load the environment variables before launching the backend, and use a strong password that satisfies the bootstrap configuration's minimum length requirement.

The bootstrap mechanism creates the account only when an account with that email does not already exist. Disable the bootstrap mechanism when it is no longer needed.

### Step 6: Start the frontend

Open a second PowerShell terminal:

```powershell
cd frontend
npm ci
npm run dev
```

Vite starts the frontend development server and prints its local URL in the terminal, typically:

```text
http://localhost:5173
```

Keep the backend running while using the frontend. The Vite configuration proxies `/api` requests to the backend at `http://localhost:8080`.

### Step 7: Build and lint the frontend

From the `frontend` directory:

```powershell
npm run build
npm run lint
```

The build command performs TypeScript checks and generates the production frontend assets.

### Step 8: Run the backend tests

From the repository root:

```powershell
cd backend
.\\mvnw.cmd test
```

Docker Desktop must be running for tests that use Testcontainers.

### Step 9: Stop the database

When finished, run this command from the repository root:

```powershell
docker compose down
```

The named PostgreSQL volume is retained, so the database data remains available for the next run.

To remove the database volume and its stored data as well, use the following command only when you intentionally want to reset the local database:

```powershell
docker compose down -v
```

### Troubleshooting

**Database connection failure**

* Confirm Docker Desktop is running.
* Check `docker compose ps` and the PostgreSQL health status.
* Verify `POSTGRES\_URL`, `POSTGRES\_DB`, `POSTGRES\_USER`, and `POSTGRES\_PASSWORD`.
* Confirm port `5434` is available on the host.

**Missing environment variables**

* Ensure the root `.env` contains the required variables.
* Ensure those variables are loaded into the PowerShell process that starts Spring Boot.
* Verify `JWT\_SECRET\_BASE64` contains a valid Base64-encoded secret of at least 32 bytes after decoding.

**Frontend API requests fail**

* Confirm the backend is running on port `8080`.
* Confirm the Vite proxy configuration targets `http://localhost:8080`.
* Inspect the browser developer tools and backend logs for the specific request error.

**Database migration fails**

* Inspect the backend startup logs for the Flyway error.
* Verify the database credentials and connectivity.
* If the database has previously been initialized, do not delete the volume unless you intend to discard its data.

## 10\. Project Objective

IMS is an ongoing software engineering project intended to demonstrate the design and implementation of a full-stack business application with explicit domain boundaries, maintainable backend architecture, secure API access, reliable persistence, automated testing, and a professional development workflow.

The project will continue to evolve as additional inventory use cases are implemented and tested.

