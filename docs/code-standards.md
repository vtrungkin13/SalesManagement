# Code Standards

This document establishes the architecture patterns, code styles, and engineering guidelines for the Sales Management System codebase.

---

## 🏛️ Layered Architecture Pattern

The system strictly adheres to a standard 4-layer Spring Boot structure:

```
[ Client Request ]
       │
       ▼
[ Controller Layer ] (Request Validation, Endpoint Definition, DTO Mapping)
       │
       ▼
  [ Service Layer ]  (Business Logic, Transaction Management, Security Audits)
       │
       ▼
[ Repository Layer ] (Data Access Object, Database Queries)
       │
       ▼
   [ Database ]      (SQL Server Storage)
```

---

## 📐 General Coding Principles

### 1. Java Version Capabilities
- Utilize **Java 21** features (e.g., Records for DTOs, Pattern Matching, Switch expressions).
- All DTO definitions must be defined as Java Records for immutability and concise syntax.

### 2. Multi-Tenant Safety & Data Leakage Prevention
- **Access Verification**: Before performing operations on tenant-scoped entities (e.g., Customers, Orders, Warehouses), you must verify the ownership:
  ```java
  TenantSecurityUtil.verifyTenantAccess(entity.getTenant().getId());
  ```
- **Context Management**: Ensure that `TenantContext.clear()` is always called at the end of thread processes (e.g., inside filters) to avoid tenant ID leakage across recycled HTTP request threads.

### 3. Dependency Injection
- Always use **Constructor Injection** instead of field injection (`@Autowired`).
- Annotate class definitions with Lombok's `@RequiredArgsConstructor` and mark variables as `private final` to enforce immutability.

### 4. DTO Mapping
- Avoid writing manual mapping code. Use **MapStruct** mapper interfaces.
- Component model must be specified as `spring` to enable CDI:
  ```java
  @Mapper(componentModel = "spring")
  public interface CustomerMapper { ... }
  ```

### 5. Transaction Management
- Apply `@Transactional` (from `jakarta.transaction.Transactional`) on the class level for all service implementations.
- Write operations that alter state should be explicitly checked. If a read-only transaction is needed, configure the database profile or annotate accordingly to optimize performance.

### 6. Exception Handling
- Do not catch exceptions and swallow them. Allow them to bubble up to the `GlobalExceptionHandler`.
- Return standardized JSON response payloads for API errors with clear HTTP status codes.

### 7. Performance & Concurrency Optimization
- **Bulk Operations**: When importing or creating multiple entities (e.g., product imports), do not perform queries or saves inside a loop. Retrieve dependencies in batch (using IN clauses) and use Spring Data's `saveAll()` to minimize database roundtrips.
- **Atomic Database Updates**: To prevent race conditions and lost updates in concurrent environments (such as inventory/stock changes), execute updates atomically in the database using `@Modifying` update queries (e.g., `SET quantity = quantity - :amount WHERE id = :id AND quantity >= :amount`) rather than reading to memory, updating, and saving back.
