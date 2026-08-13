# EazyBank Microservices — Production Starter

Production-ready starter cho hệ thống microservices ngân hàng, được xây dựng trên Spring Boot 4.0, Java 21 và Spring Cloud 2025.1.0.

## Stack công nghệ

| Category | Technology |
|----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.0, Spring Cloud 2025.1.0 |
| Build | Maven (multi-module, BOM pattern) |
| Database | PostgreSQL 16 + Flyway migration |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway (WebFlux) |
| Configuration | Spring Cloud Config Server |
| Messaging | Apache Kafka + Spring Cloud Stream |
| Resilience | Resilience4j (Circuit Breaker, Retry, Rate Limiter) |
| Inter-service | Spring Cloud OpenFeign |
| Security | Keycloak OAuth2/OIDC + JWT |
| Observability | OpenTelemetry, Micrometer, Prometheus |
| Testing | JUnit 5, Mockito, AssertJ, Testcontainers |
| Container | Docker, Google Jib |
| Local infra | Docker Compose (Postgres, Kafka, Redis, Keycloak) |

## Cấu trúc dự án

```
microservices/
├── pom.xml                      # Root aggregator POM
├── eazy-bom/                    # BOM — quản lý version tập trung
├── eazy-common/                 # Shared library (entity, DTO, exception, audit)
├── eazy-common-test/            # Shared test utilities (base test classes)
├── services/
│   ├── configserver/            # Spring Cloud Config Server (port 8071)
│   ├── discoveryserver/         # Netflix Eureka Server (port 8070)
│   ├── gatewayserver/           # API Gateway + Security (port 8072)
│   ├── accounts/                # Accounts + Customers (port 8080)
│   ├── cards/                   # Cards (port 9000)
│   ├── loans/                   # Loans (port 8090)
│   └── message/                 # Kafka event processor (port 9010)
├── infrastructure/
│   ├── docker/                  # Docker Compose cho local dev
│   ├── kubernetes/              # K8s manifests (sẽ bổ sung)
│   └── helm/                    # Helm charts (sẽ bổ sung)
├── docs/                        # Tài liệu, ADR
├── scripts/                     # Utility scripts
└── .github/workflows/           # CI/CD (sẽ bổ sung)
```

## Kiến trúc & Pattern đã implement

### 1. Multi-Module + BOM Pattern

Toàn bộ version dependency được quản lý tập trung trong `eazy-bom/pom.xml`. Các service kế thừa BOM qua `<parent>` và không cần khai báo version riêng.

```xml
<parent>
    <groupId>com.eazybytes</groupId>
    <artifactId>eazy-bom</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <relativePath>../../eazy-bom/pom.xml</relativePath>
</parent>
```

**Lợi ích:** Một nơi duy nhất để bump version, không bị version drift giữa các service.

### 2. Shared Library (`eazy-common`)

Code dùng chung được extract vào `eazy-common`:

| Package | Nội dung |
|---------|----------|
| `com.eazybytes.common.entity` | `BaseEntity` (JPA audit fields) |
| `com.eazybytes.common.dto` | `ErrorResponseDto`, `ResponseDto` |
| `com.eazybytes.common.exception` | `GlobalExceptionHandler`, `ResourceNotFoundException`, `AlreadyExistsException` |
| `com.eazybytes.common.audit` | `AuditAwareBase` (JPA auditing) |
| `com.eazybytes.common.config` | `JacksonConfig` (date/time serialization) |

Service chỉ cần scan package common:

```java
@SpringBootApplication(scanBasePackages = {"com.eazybytes.accounts", "com.eazybytes.common"})
```

### 3. Database Migration (Flyway)

Mỗi service có schema PostgreSQL riêng và migration scripts tại `src/main/resources/db/migration/`:

```
accounts → accounts_schema
cards    → cards_schema
loans    → loans_schema
```

Cấu hình trong `application.yml`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate    # KHÔNG dùng update trong production
  flyway:
    enabled: true
    locations: classpath:db/migration
    schemas: accounts_schema
    default-schema: accounts_schema
```

**Quy tắc:** Luôn dùng Flyway migration để thay đổi schema, không bao giờ để Hibernate auto-create.

### 4. Global Exception Handling

`GlobalExceptionHandler` trong `eazy-common` xử lý tất cả exceptions:

- `MethodArgumentNotValidException` → 400 với map field errors
- `ConstraintViolationException` → 400
- `ResourceNotFoundException` → 404
- `AlreadyExistsException` → 409
- `Exception` → 500 (catch-all)

Response format thống nhất qua `ErrorResponseDto` (apiPath, errorCode, errorMessage, errorTime).

### 5. JPA Auditing

`BaseEntity` cung cấp 4 field audit tự động: `createdAt`, `createdBy`, `updatedAt`, `updatedBy`.

Mỗi service implement `AuditAwareBase` để xác định actor:

```java
@Component("auditAwareImpl")
public class AuditAwareImpl extends AuditAwareBase {
    @Override
    protected String getServiceName() {
        return "ACCOUNTS_MS";
    }
}
```

### 6. Service Discovery (Eureka)

`discoveryserver` chạy Netflix Eureka. Các service tự đăng ký và gateway dùng `lb://SERVICE-NAME` để load-balance client-side.

```yaml
eureka:
  client:
    fetchRegistry: true
    registerWithEureka: true
    serviceUrl:
      defaultZone: http://localhost:8070/eureka/
```

### 7. API Gateway + Resilience

`gatewayserver` định nghĩa routes và áp dụng resilience patterns:

| Route | Pattern |
|-------|---------|
| `/eazybank/accounts/**` | Circuit Breaker (fallback → `/contactSupport`) |
| `/eazybank/loans/**` | Retry (3 lần, exponential backoff) |
| `/eazybank/cards/**` | Rate Limiter (Redis-based, 1 req/s) |

Security dùng Keycloak JWT với role-based access:
- GET → permit all
- `/eazybank/accounts/**` → `ROLE_ACCOUNTS`
- `/eazybank/cards/**` → `ROLE_CARDS`
- `/eazybank/loans/**` → `ROLE_LOANS`

### 8. Event-Driven Messaging (Kafka)

Accounts service publish event qua Spring Cloud Stream khi tạo account:

```java
streamBridge.send("sendCommunication-out-0", accountsMsgDto);
```

`message` service consume và xử lý:

```yaml
spring:
  cloud:
    function:
      definition: email|sms
    stream:
      bindings:
        emailsms-in-0:
          destination: send-communication
```

### 9. Testing Strategy

| Layer | Annotation | Purpose |
|-------|-----------|---------|
| Service | `@ExtendWith(MockitoExtension.class)` | Unit test business logic, mock dependencies |
| Controller | `@WebMvcTest` | Test REST endpoints, validation, response format |
| Repository | `@DataJpaTest` + Testcontainers | Test JPA queries với PostgreSQL thật |
| Integration | `@SpringBootTest` + Testcontainers | Full context, real DB + Kafka |

**Lưu ý Spring Boot 4.0 package changes:**
- `@WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
- `@DataJpaTest` → `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`
- `@MockBean` → `@MockitoBean` (`org.springframework.test.context.bean.override.mockito.MockitoBean`)
- `@AutoConfigureTestDatabase` → `org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase`

### 10. Configuration Externalization

Secrets không hardcode trong source. Dùng environment variables với default cho local dev:

```yaml
spring:
  datasource:
    username: ${DB_USERNAME:eazybank}
    password: ${DB_PASSWORD:eazybank}
```

Production sẽ dùng Vault/K8s Secrets (bổ sung ở phase sau).

## Chạy dự án

### Prerequisites

- Java 21
- Maven 3.9+
- Docker (cho local infrastructure và Testcontainers)

### Bước 1: Khởi động infrastructure

```bash
cd infrastructure/docker
docker compose up -d
```

Khởi động: PostgreSQL (5432), Kafka (9092), Redis (6379), Keycloak (7080).

### Bước 2: Build toàn bộ

```bash
mvn clean install
```

### Bước 3: Chạy services theo thứ tự

```bash
# 1. Infrastructure services
mvn -pl services/configserver spring-boot:run
mvn -pl services/discoveryserver spring-boot:run

# 2. Business services
mvn -pl services/accounts spring-boot:run
mvn -pl services/cards spring-boot:run
mvn -pl services/loans spring-boot:run

# 3. Message processor
mvn -pl services/message spring-boot:run

# 4. Gateway (chạy cuối)
mvn -pl services/gatewayserver spring-boot:run
```

### Truy cập

| Service | URL |
|---------|-----|
| Eureka Dashboard | http://localhost:8070 |
| API Gateway | http://localhost:8072 |
| Accounts API | http://localhost:8080/api |
| Swagger UI (accounts) | http://localhost:8080/swagger-ui.html |

## Chạy tests

```bash
# Full test suite
mvn test

# Test một service cụ thể
mvn -pl services/accounts test

# Test một class
mvn -pl services/accounts test -Dtest=AccountsServiceImplTest

# Skip Testcontainers tests (khi không có Docker)
# Các test này tự skip nếu không có Docker environment
```

## Thêm service mới

1. Tạo thư mục `services/<name>/`
2. Copy `pom.xml` từ service tương tự, đổi `artifactId`
3. Thêm module vào root `pom.xml`
4. Tạo `src/main/java/com/eazybytes/<name>/` với `@SpringBootApplication`
5. Tạo `src/main/resources/application.yml` và `db/migration/` (nếu có DB)
6. Tạo test theo pattern ở trên

## Roadmap

### Đã hoàn thành
- [x] Multi-module structure + BOM
- [x] PostgreSQL + Flyway migrations
- [x] Unit tests (service, controller, repository)
- [x] Vault integration + secrets externalization
- [x] Avro schemas + Schema Registry config
- [x] Dead Letter Queue
- [x] Outbox pattern
- [x] API versioning (`/api/v1`)
- [x] Istio mTLS config
- [x] CORS configuration
- [x] Business metrics (Prometheus counters)
- [x] Structured logging (logback JSON in prod)
- [x] CI/CD pipeline (GitHub Actions)
- [x] Kubernetes manifests + Helm charts (skeleton)
- [x] ArgoCD GitOps config
- [x] Terraform IaC modules (skeleton)
- [x] Multi-tenancy (tenant_id)
- [x] Architecture Decision Records (13 ADRs)

### Chưa hoàn thành
- [ ] Contract tests (Spring Cloud Contract)
- [ ] Distributed tracing (OpenTelemetry + Tempo) — cần deployment
- [ ] Performance tests (Gatling)
- [ ] Full Helm chart implementation (sub-charts cho từng service)
- [ ] Terraform actual resource definitions (hiện là skeleton)

