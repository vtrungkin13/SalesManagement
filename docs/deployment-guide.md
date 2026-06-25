# Deployment & Setup Guide

This guide explains how to compile, run, test, and deploy the Multi-Tenant Sales Management System.

---

## 🛠️ Local Environment Execution

### 1. Configure Environment Variables
Before running the application, set up the following environment variables or customize their defaults in `src/main/resources/application.properties`:

| Variable | Description | Default Value |
|:---|:---|:---|
| `DB_USERNAME` | SQL Server Database Username | `sa` |
| `DB_PASSWORD` | SQL Server Database Password | `Trungkien132@` |
| `JWT_SECRET_KEY` | JWT signing secret key (Base64-encoded) | (Preconfigured long secret key) |

### 2. Run Database
Ensure Microsoft SQL Server is running. The default JDBC URL expects it to run on host `sqlserver` on port `1433`. If you want to connect to a local SQL Server installation on localhost, edit the connection URL in `application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=sales;encrypt=true;trustServerCertificate=true
```

### 3. Build & Run Application
Use the Maven Wrapper to build and run the application:
```bash
# Compile and package
./mvnw clean package

# Run the boot jar
./mvnw spring-boot:run
```

---

## 🐳 Containerized Execution (Docker)

The project includes a `Dockerfile` and a `docker-compose.yml` configuration to stand up both the SQL Server instance and the Spring Boot application together.

### 1. Build and Start Services
Execute the docker-compose command in the root folder:
```bash
docker-compose up --build -d
```

### 2. Verify Deployment
Once the containers are up, the services will be running:
- **Spring Boot API**: `http://localhost:8080`
- **Swagger Documentation API**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Schema**: `http://localhost:8080/v3/api-docs`

---

## 🧪 Testing

To run the automated unit and integration tests:
```bash
./mvnw test
```
