# Codebase Summary

This document provides a technical walkthrough of the codebase, detailing directory structures, file distributions, and the responsibilities of each module.

## 📊 File Count & LOC Summary

- **Total Java Source Files**: ~80 files
- **Total Lines of Code**: ~2,500 LOC
- **Primary Package**: `pers.project.salesmanagement`

### Directory Distribution

| Package Directory | Key Responsibilities |
|:---|:---|
| `/controller` | REST endpoints exposing auth, user administration, tenant setup, role management, and sales orders. |
| `/dto` | Request and response representations using Java 21 Records for immutability and clean JSON mapping. |
| `/entity` | JPA/Hibernate database entities containing annotations for SQL Server schemas. Includes statuses enum package. |
| `/exception` | Global exception handling strategy and custom exception subclasses. |
| `/mapper` | MapStruct-driven conversion mapping interfaces between Entities and DTO records. |
| `/repository` | Spring Data JPA repositories with query abstractions. |
| `/security` | Filters, configuration details, rate limiters, JWT utilities, and multi-tenancy context handlers. |
| `/service` | Business logic interfaces and transactionally-orchestrated implementations. |

---

## 🔑 Crucial Implementation Files

### 🛡️ Multi-Tenancy Core
- [TenantContext.java](file:///d:/Java/SalesManagement/src/main/java/pers/project/salesmanagement/security/TenantContext.java): ThreadLocal holding the current thread's active tenant UUID.
- [TenantSecurityUtil.java](file:///d:/Java/SalesManagement/src/main/java/pers/project/salesmanagement/security/TenantSecurityUtil.java): Utility methods providing secure access verification and entity ownership checks.
- [JwtAuthenticationFilter.java](file:///d:/Java/SalesManagement/src/main/java/pers/project/salesmanagement/security/JwtAuthenticationFilter.java): Extracts tenant ID claim from the JWT token and binds it to the context, clearing it in the `finally` block to prevent thread pool leakage.

### 💼 Key Business Services
- [AuthServiceImpl.java](file:///d:/Java/SalesManagement/src/main/java/pers/project/salesmanagement/service/impl/AuthServiceImpl.java): Orchestrates user registration, authentication, rate limits checking, and refresh token issuance.
- [SalesOrderServiceImpl.java](file:///d:/Java/SalesManagement/src/main/java/pers/project/salesmanagement/service/impl/SalesOrderServiceImpl.java): Process logic for sales orders, resolving variant pricing, creating order items, and persisting changes atomically.
- [ProductServiceImpl.java](file:///d:/Java/SalesManagement/src/main/java/pers/project/salesmanagement/service/impl/ProductServiceImpl.java): Handles creation and bulk imports of products using batch lookups and bulk saves to minimize DB roundtrips.
- [TenantServiceImpl.java](file:///d:/Java/SalesManagement/src/main/java/pers/project/salesmanagement/service/impl/TenantServiceImpl.java): Management of tenants (registration, listing).
- [InventoryRepository.java](file:///d:/Java/SalesManagement/src/main/java/pers/project/salesmanagement/repository/InventoryRepository.java): Implements concurrent-safe atomic update methods (`deductQuantity`) to manage stock correctly.
