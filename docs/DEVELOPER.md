# EazyBank Developer Guide

## Prerequisites

- Java 21 (OpenJDK hoặc tương đương)
- Maven 3.9+
- Docker (cho local infrastructure và Testcontainers tests)

## Local Setup

### 1. Khởi động infrastructure

```bash
cd infrastructure/docker
docker compose up -d
```

Services khởi động:
- PostgreSQL: `localhost:5432` (user/pass: `eazybank`/`eazybank`)
- Kafka: `localhost:9092`
- Redis: `localhost:6379`
- Keycloak: `localhost:7080` (admin/admin)

### 2. Build project

```bash
mvn clean install
```

### 3. Chạy services

Theo thứ tự:

```bash
# Infrastructure
mvn -pl services/configserver spring-boot:run
mvn -pl services/discoveryserver spring-boot:run

# Business
mvn -pl services/accounts spring-boot:run
mvn -pl services/cards spring-boot:run
mvn -pl services/loans spring-boot:run

# Message
mvn -pl services/message spring-boot:run

# Gateway (cuối cùng)
mvn -pl services/gatewayserver spring-boot:run
```

## Testing

```bash
# Toàn bộ test
mvn test

# Một service
mvn -pl services/accounts test

# Một class test
mvn -pl services/accounts test -Dtest=AccountsServiceImplTest

# Skip Testcontainers (không Docker)
# Test tự skip với @Testcontainers(disabledWithoutDocker = true)
```

## Cấu trúc service chuẩn

```
services/<name>/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/eazybytes/<name>/
    │   │   ├── <Name>Application.java     # @SpringBootApplication
    │   │   ├── config/                    # JPA auditing config
    │   │   ├── controller/                # REST controllers
    │   │   ├── service/                   # interfaces + impl
    │   │   ├── repository/                # JPA repositories
    │   │   ├── entity/                    # JPA entities
    │   │   ├── dto/                       # DTOs
    │   │   ├── mapper/                    # entity ↔ dto
    │   │   ├── exception/                 # service-specific exceptions
    │   │   ├── audit/                     # AuditAwareImpl
    │   │   └── constants/                 # constants
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/              # Flyway scripts
    └── test/
        └── java/com/eazybytes/<name>/
            ├── controller/                # @WebMvcTest
            ├── service/                   # Mockito unit tests
            └── repository/                # @DataJpaTest + Testcontainers
```

## Thêm service mới

1. Tạo thư mục `services/<name>/`
2. Copy `pom.xml` từ service tương tự, đổi `artifactId`
3. Thêm `<module>services/<name></module>` vào root `pom.xml`
4. Tạo package `com.eazybytes.<name>` với main class:
   ```java
   @SpringBootApplication(scanBasePackages = {"com.eazybytes.<name>", "com.eazybytes.common"})
   public class <Name>Application { ... }
   ```
5. Tạo `application.yml` (port riêng, DB config, Eureka, management)
6. Nếu có DB: tạo `db/migration/V1__*.sql` + `config/JpaAuditingConfig.java`
7. Viết tests theo pattern ở trên

## Code conventions

- Dùng Lombok (`@Data`, `@Getter`, `@Setter`, `@AllArgsConstructor`)
- Constructor injection thay vì field injection
- `@RestController` với `ResponseEntity` return type
- Exception: throw `ResourceNotFoundException` / `AlreadyExistsException` từ common
- Không hardcode secrets — dùng `${ENV_VAR:default}`
- Flyway migration thay vì `ddl-auto: update`
