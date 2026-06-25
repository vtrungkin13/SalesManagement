# Project Roadmap

This document outlines the current status of the project and details the planned phases for future development and scaling.

---

## 📍 Current State (Phase 1: Foundation)
- [x] Multi-Tenant security and session management architecture.
- [x] JPA Data structures for Sales, Procurement, Return Orders, Warehouse, Suppliers, and Customers.
- [x] JWT Stateless authentication, Refresh token rotation, and Login Rate Limiting.
- [x] DTO Mappers using MapStruct.
- [x] Controller layers for CRUD operations on Users, Roles, Orders, and Tenants.

---

## ⚡ Future Development Phases

### Phase 2: Automated Multi-Tenant Query Filtering (Near Term)
- **Goal**: Implement automatic schema-level or Hibernate-level tenant filters to avoid writing manual service-level verification boilerplate.
- **Tasks**:
  - Integrate Hibernate `@Filter` and `@FilterDef` on all tenant-scoped entities.
  - Setup an Aspect (`@Aspect`) to automatically bind the active `TenantContext.getTenantId()` to the Hibernate Session filter when a repository call starts.

### Phase 3: Reporting & Analytics Dashboard (Medium Term)
- **Goal**: Provide tenants with rich, visual data regarding sales, product velocity, low-stock warnings, and revenue trends.
- **Tasks**:
  - Implement read-only optimized repository queries using JPA projections or raw SQL for quick stats aggregates.
  - Add API endpoints exposing stats: Top Selling Products, Revenue per month, low stock alerts.

### Phase 4: Notification Service (Medium Term)
- **Goal**: Alert users when inventory levels dip below thresholds or when critical orders are returned.
- **Tasks**:
  - Introduce asynchronous event publishing using Spring Events (`@EventListener` + `@Async`).
  - Send email/SMS notifications to warehouse staff on critical events.

### Phase 5: Modern SPA Frontend Integration (Long Term)
- **Goal**: Build a premium web client dashboard (React/TypeScript or Next.js) tailored for desktop and mobile operations.
- **Tasks**:
  - Initialize Vite React project.
  - Connect client-side routing and configure security tokens persistence.
