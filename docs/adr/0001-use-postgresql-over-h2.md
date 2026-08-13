# ADR 0001: Sử dụng PostgreSQL thay vì H2

## Status
Accepted

## Context
Codebase ban đầu (section 2-20) dùng H2 in-memory database cho tất cả services. Điều này phù hợp cho tutorial nhưng không thể dùng trong production vì:
- Dữ liệu mất khi restart
- Không hỗ trợ concurrent access tốt
- Không có backup/recovery
- Hibernate `ddl-auto: update` gây nguy hiểm khi deploy

## Decision
Chuyển sang PostgreSQL 16 cho tất cả business services (accounts, cards, loans). Mỗi service dùng schema riêng để isolation.

## Consequences
- **Tích cực:** Dữ liệu bền vững, hỗ trợ production, backup được
- **Tiêu cực:** Cần Docker/PostgreSQL cho local dev và test
