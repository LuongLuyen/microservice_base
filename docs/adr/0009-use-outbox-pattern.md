# ADR 0009: Sử dụng Outbox Pattern cho messaging

## Status
Accepted

## Context
Accounts service cần publish event khi tạo account, nhưng phải đảm bảo atomic giữa DB write và message publish.

## Decision
Sử dụng Outbox Pattern: lưu event vào bảng `outbox` trong cùng transaction với business data. Một scheduled task đọc các pending events và publish qua Kafka.

## Consequences
- **Tích cực:** Atomic DB write + event, không mất message, dễ retry
- **Tiêu cực:** Thêm bảng và background task, cần cleanup published events
