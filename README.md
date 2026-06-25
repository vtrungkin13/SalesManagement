# Sales Management System (Multi-Tenant)

A premium, secure, and scalable multi-tenant SaaS application built with **Spring Boot 3.5**, **Java 21**, **Spring Security**, **JPA/Hibernate**, and **Microsoft SQL Server**.

## 🚀 Key Features

- **Multi-Tenant Isolation**: Complete logical data separation per tenant with runtime validation.
- **Secure Authentication**: Stateless JWT-based authentication, login rate limiting, and secure refresh token rotation.
- **Role-Based Access Control (RBAC)**: Fine-grained user permissions and role management.
- **Sales & Order Management**: Comprehensive order creation, tracking, customer loyalty points, and discounts.
- **Inventory & Warehouse**: Complete tracking of products, variants, stock movements, and warehouse management.
- **Purchase & Returns**: Lifecycle management for Goods Receipts, Purchase Orders, and Return Orders.

---

## 🛠️ Tech Stack

- **Backend Framework**: Spring Boot 3.5.15
- **Language**: Java 21
- **Database**: Microsoft SQL Server
- **Data Access**: Spring Data JPA / Hibernate
- **Object Mapping**: MapStruct 1.6.3
- **Utility**: Lombok
- **Security**: Spring Security & JSON Web Tokens (jjwt 0.12.6)

---

## 📂 Project Directory Structure

```
pers.project.salesmanagement
├── controller       # REST Controllers (Auth, Users, Orders, Tenants, Roles)
├── dto              # Data Transfer Objects (Requests & Responses)
├── entity           # JPA Entities (Tenant, AppUser, Product, SalesOrder, etc.)
│   └── status       # Entity Enums (ProductStatus, UserStatus, etc.)
├── exception        # Exception Definitions & Global Exception Handler
├── mapper           # MapStruct Mapper Interfaces
├── repository       # Spring Data JPA Repositories
├── security         # Security Configurations, JWT Filters, & Tenant Context
└── service          # Business Logic Interfaces
    └── impl         # Service Implementations
```

---

## ⚙️ Getting Started

### Prerequisites
- JDK 21
- Maven 3.8+
- SQL Server (or Docker to run the containerized setup)

### Running Locally
1. Configure database connection parameters in `src/main/resources/application.properties` or set the environment variables:
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET_KEY`
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Running with Docker Compose
```bash
docker-compose up --build
```
