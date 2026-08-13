# ADR 0013: Sử dụng tenant_id cho multi-tenancy

## Status
Accepted

## Context
Hệ thống cần hỗ trợ nhiều tenant (khách hàng) với data isolation.

## Decision
Thêm `tenant_id` column vào tất cả entities qua `BaseEntity`. Tenant context được truyền qua HTTP header `X-Tenant-Id`, gateway filter propagates tới downstream services.

## Consequences
- **Tích cực:** Đơn giản, một DB shared, dễ query
- **Tiêu cực:** Cần đảm bảo mọi query đều filter theo tenant_id (row-level security)
