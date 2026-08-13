# ADR 0010: Sử dụng URL-based API versioning

## Status
Accepted

## Context
API cần version để support backward compatibility khi có breaking changes.

## Decision
Sử dụng URL-based versioning: `/api/v1/...`, `/api/v2/...`. Controller mapping dùng `@RequestMapping("/api/v1")`.

## Consequences
- **Tích cực:** Đơn giản, explicit, dễ test
- **Tiêu cực:** Cần duplicate controllers khi có version mới
