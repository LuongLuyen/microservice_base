# ADR 0004: Sử dụng Eureka cho service discovery

## Status
Accepted

## Context
Các services cần tìm nhau mà không hardcode IP/port. Gateway cần load-balance tới backend services.

## Decision
Sử dụng Netflix Eureka làm service discovery. Gateway dùng `lb://SERVICE-NAME` để client-side load-balance qua Spring Cloud LoadBalancer.

## Consequences
- **Tích cực:** Service tự đăng ký, không hardcode, auto-failover
- **Tiêu cực:** Thêm một infrastructure component cần quản lý
