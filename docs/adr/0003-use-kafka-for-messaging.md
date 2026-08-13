# ADR 0003: Sử dụng Kafka cho messaging

## Status
Accepted

## Context
Cần giao tiếp bất đồng bộ giữa accounts service và message service (gửi email/SMS khi tạo account).

## Decision
Sử dụng Apache Kafka + Spring Cloud Stream. Kafka được chọn vì:
- High throughput, low latency
- Event replay capability
- Mature ecosystem, hỗ trợ tốt trong Spring Cloud

## Consequences
- **Tích cực:** Scalable, reliable, event-driven architecture
- **Tiêu cực:** Cần quản lý Kafka cluster, thêm operational overhead
