# ADR 0006: Sử dụng Multi-Module Maven với BOM

## Status
Accepted

## Context
Nhiều services dùng chung dependencies và version. Nếu mỗi service quản lý version riêng sẽ gây version drift.

## Decision
Sử dụng multi-module Maven với `eazy-bom` (Bill of Materials) làm parent. Tất cả services kế thừa từ BOM, version được quản lý tập trung.

## Consequences
- **Tích cực:** Một nơi bump version, không drift, dễ maintain
- **Tiêu cực:** Cấu trúc phức tạp hơn, cần hiểu Maven reactor
