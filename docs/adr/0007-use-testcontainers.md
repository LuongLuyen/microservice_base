# ADR 0007: Sử dụng Testcontainers cho integration tests

## Status
Accepted

## Context
Integration tests cần database thật để verify JPA queries, migrations, và end-to-end flow. H2 không đủ vì khác syntax với PostgreSQL.

## Decision
Sử dụng Testcontainers để spin up PostgreSQL và Kafka containers trong test. Tests tự skip khi không có Docker (`disabledWithoutDocker = true`).

## Consequences
- **Tích cực:** Test với database thật, chính xác, không cần mock DB
- **Tiêu cực:** Cần Docker, test chậm hơn (startup container)
